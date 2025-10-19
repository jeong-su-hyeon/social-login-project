package com.sociallogin.social_login_project.Security;

import com.sociallogin.social_login_project.Entity.User;
import com.sociallogin.social_login_project.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class UserPrincipalService implements UserDetailsService {

    // 사용자가 입력한 Email을 기준으로 DB에서 해당 사용자를 조회하는 로직

    @Autowired // UserRepository 의존성 주입
    private UserRepository userRepository;

    // [로딩] email로 사용자 정보를 로딩
    // 로그인 시 사용자 정보를 DB에서 조회하고 검증한 뒤,
    // Spring Security가 이해할 수 있는 형태로 래핑해서 반환하는 역할
    @Override
    public UserPrincipal loadUserByUsername(String _email) throws UsernameNotFoundException {
        log.info("\n[UserPrincipalService] 서비스 - loadUserByUsername 메서드 실행"); // 디버깅

        // 0) 사용자 정보 조회 (userName)
        User user = userRepository.findByEmail(_email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 1) 사용자 정보 추출
        Long id = user.getId();
        String email = user.getEmail();
        String password = user.getPassword();

        // 2) 권한 리스트 생성 & 권한 추가
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLL_USER"); // 기본 권한 부여
        authorities.add(authority); // 권한 추가

        // 3) 사용자 정보와 권한을 담은 UserPrincipal 객체 생성
        // Spring Security의 인증 컨텍스트에 저장 됨
        UserPrincipal userPrincipal = new UserPrincipal(id, email, password, authorities);

        return userPrincipal;
    }
}
