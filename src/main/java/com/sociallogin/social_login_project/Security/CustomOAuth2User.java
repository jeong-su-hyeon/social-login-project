package com.sociallogin.social_login_project.Security;

import com.sociallogin.social_login_project.DTO.OAuthAttributes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;

// OAuth2 로그인 사용자의 정보를 확장해 커스텀 유저 객체로 만드는 클래스
public class CustomOAuth2User extends DefaultOAuth2User {

    private static final long serialVersionUID = 1L; // 직렬화를 위한 UID

    // (확장 용도) OAuth2 인증 제공자로부터 받은 데이터 외!!로 별도로 관리하는 사용자 정보
    private Long userId;        // 사용자 ID
    private String userName;    // 사용자 이름 or 이메일
    private String nickName;    // 사용자 닉네임

    // [생성자] 커스텀 OAuth2 사용자 객체
    public CustomOAuth2User(
            Long userId,
            String userName,
            String nickName,
            Collection<? extends GrantedAuthority> authorities,
            OAuthAttributes attributes
    ) {
        // [부모 호출] 부모 클래스인 DefaultOAuth2User의 생성자 호출
        // getAttributes() : 사용자 정보 Map<>
        // getNameAttributeKey() : 식별 키
        super(authorities, attributes.getAttributes(), attributes.getNameAttributeKey());

        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
    }

    // [Getter]
    // OAuth2User의 getName() 오버라이딩 -> 사용자명 반환
    @Override
    public String getName() { return userName; }

    // 시스템 사용자 이름 반환
    public String getUserName() { return userName; }

    // 시스템 닉네임 반환
    public String getNickName() { return nickName; }

    // 시스템 사용자 ID 반환
    public Long getUserId() { return userId; }
}
