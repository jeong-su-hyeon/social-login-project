package com.sociallogin.social_login_project.Security;

import com.sociallogin.social_login_project.DTO.OAuthAttributes;
import com.sociallogin.social_login_project.Entity.User;
import com.sociallogin.social_login_project.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
// 소셜 로그인 후, 사용자 정보를 처리하는 커스텀 서비스(로직) 클래스
// -> 사용자 정보를 가져오고 가공함
// -> 사용자 정보를 받아와 로그인 및 회원 등록까지 자동화
// 1) 외부 인증 제공자(구글, 카카오 등)에서 사용자 정보를 받아옴 (=> 이름, 프로필 사진 등의 정보)
// 2) 받은 사용자 정보를 OAuthAttributes(DTO)로 변환 (중요 -> 소셜마다 응답 형태가 다르기 때문에 각각 다른 파싱 로직이 필요함)
// 3) 기존 사용자인지 확인 -> 없으면 회원가입을 진행
// 4) 사용자 정보를 담은 CustomOAuth2User 객체를 생성해 반환 -> Spring Security 내부에서 인증된 사용자 정보를 담는 역할
//      -> 사용자 아이디, 이메일, 닉네임 등 필요한 정보를 담아 컨트롤러나 다른 서비스에서 사용하도록 구현함
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    // [멤버 필드]
    // 1) users DB 접근
    private final UserRepository userRepository;

    // 2) SecurityConfig에서 주입 받는 대신 직접 생성해 사용 (순환참조 방지)
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // [메서드]
    // OAuth2 로그인 시, 사용자 정보를 불러오고 처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("\n[서비스] CustomOAuth2UserService - loadUser 메서드 실행"); // 디버깅

        // 1 [사용자 정보 조회] 기본 OAuth2UserService를 통해 로그인 된 사용자의 정보를 조회
        // 사용자가 구글, 깃허브 등을 통해 로그인 하면 토큰을 기반으로 사용자 정보가 JSON 형태로 넘어옴
        // 그것을 OAuth2User 객체로 변환
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 2 [플랫폼 구분] 어떤 플랫폼으로 로그인했는지 구분
        // OAuth2 서비스 등록 ID (ex. google, naver, kakao, github)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 사용자 식별 키 (ex. sub, id)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        log.info("\n[서비스] OAuth2 loadUser() registrationId = " + registrationId);
        log.info("\n[서비스] OAuth2 loadUser() userNameAttributeName = " + userNameAttributeName);


        // 3 [사용자 정보 매핑] OAuthAttributes 객체로 사용자 정보 매핑
        // DTO 클래스를 통해 각 플랫폼의 JSON 데이터를 String 형태로 변환
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 속성 정보 추출
        String nameAttributeKey = attributes.getNameAttributeKey(); // 사용자 식별 키 이름
        String name = attributes.getName();                         // 사용자 이름
        String email = attributes.getEmail();                       // 사용자 이메일
        String picture = attributes.getPicture();                   // 프로필 사진
        String id = attributes.getId();                             // 소셜 서비스 고유 ID
        String socialType = registrationId;                         // 소셜 로그인 타입 (google, naver, kakao, github)

        // [Github] 깃허브는 기본 제공 정보에 이메일이 없을 수 있음
        // -> API 를 통해 수동 조회
        if ("github".equals(registrationId) && email == null) {
            log.info("\n[서비스] OAuth2 loadUser() - 깃허브 액세스 토큰 = "
                    + userRequest.getAccessToken().getTokenValue());

            email = getEmailFromGithub(userRequest.getAccessToken().getTokenValue());
            log.info("\n[서비스] OAuth2 loadUser() - 깃허브 이메일 = " + email);
        }

         // 인증 제공자에 따라 socialType 분기 처리
//        if ("naver".equals(registrationId)) {           // 네이버
//            socialType = "naver";
//        } else if("kakao".equals(registrationId)) {     // 카카오
//            socialType = "kakao";
//        } else if ("github".equals(registrationId)) {   // 깃허브
//            socialType = "github";
//
//            // [Github] 깃허브는 기본 제공 정보에 이메일이 없을 수 있음
//            // -> API 를 통해 수동 조회
//            if(email == null) {
//                log.info("\n[서비스] OAuth2 loadUser() - 깃허브 액세스 토큰 = "
//                        + userRequest.getAccessToken().getTokenValue());
//
//                email = getEmailFromGithub(userRequest.getAccessToken().getTokenValue());
//                log.info("\n[서비스] OAuth2 loadUser() - 깃허브 이메일 = " + email);
//            }
//        } else {    // 구글
//            socialType = "google";
//        }

        // 디버깅용 로그
        log.info("\n[서비스] CustomOAuth2UserService - loadUser 메서드\n-> nameAttributeKey = " + nameAttributeKey);
        log.info("\n-> name = " + name);
        log.info("\n-> email = " + email);
        log.info("\n-> picture = " + picture);
        log.info("\n-> id = " + id);
        log.info("\n-> socialType = " + socialType);


        // null 방지를 위한 기본값 처리
        if(name == null) name = "";
        if(email == null) email = "";

        // 4 [권한 부여]
        // (기본) 일반적으로 소셜 로그인한 사용자에게 ROLE_USER 를 부여함
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER"); // 일반 사용자 권한
        authorities.add(authority);

        // 5 [users DB 등록 or 조회] (왕중요)
        // 사용자 이메일 기준으로 이미 가입된 사용자인지 확인
        Optional<User> optionalUser = userRepository.findByEmail(email);

        User createdUser = null;

        // 존재하지 않는다면 새 사용자 생성 (회원가입)
        if(!optionalUser.isPresent()) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("1234")); // 기본 비밀번호 설정 (임시 값)
            user.setSocialId(id);
            user.setSocialType(socialType);

            createdUser = userRepository.save(user);
        }
        // 존재한다면 해당 사용자 사용
        else {
            createdUser = optionalUser.orElseThrow(); // Optional에서 User 꺼냄
        }

        // 6 [인증 객체 생성 및 반환]
        // DB에 저장된 사용자 ID
        Long userId = createdUser.getId();

        // 커스텀 OAuth2User 객체 반환
        return new CustomOAuth2User(userId, email, name, authorities, attributes);
        // -> Spring Security의 인증 컨텍스트에 등록 됨
        // 로그인한 사용자의 세션 정보로 사용됨
        // Authentication.getPrincipal을 통해 사용자 정보를 확인할 수 있음
    }

    // [Github] 깃허브 API를 통해 사용자 이메일 수동 조회 메서드
    private String getEmailFromGithub(String accessToken) {
        // 깃허브의 이메일을 조회하는 공식 API 엔트포인트
        String url = "https://api.github.com/user/emails";

        // Spring에서 외부로 HTTP 요청하기 위한 객체
        RestTemplate restTemplate = new RestTemplate();

        // API 요청 시 인증 토큰 및 데이터 형식을 정의하는 객체
        HttpHeaders headers = new HttpHeaders();                    // 요청 헤더 설정
        headers.set("Authorization", "Bearer " + accessToken);      // 액세스 토큰 설정
        headers.set("Accept", "application/vnd.github.v3+json");    // 깃허브 API 응답 형식 명시

        // 헤더만 포함된 GET 요청 본문
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 이메일 정보 요청 (RestTemplate)
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
        List<Map<String, Object>> emails = response.getBody(); // (파싱) HTTP 응답 본문 중 email 목록 데이터 추출

        // 주요 이메일(Primary email) 추출
        if(emails != null) {
            for (Map<String, Object> emailData : emails) {
                if((Boolean) emailData.get("primary")) {
                    return (String) emailData.get("email");
                }
            }
        }
        return null; // 이메일을 찾지 못하면 null 반환
    }
}
