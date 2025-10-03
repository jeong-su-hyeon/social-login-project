package com.sociallogin.social_login_project.Security;

import com.sociallogin.social_login_project.Entity.User;
import com.sociallogin.social_login_project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired // UserRepository 의존성 주입
    private UserRepository userRepository;

    // [로딩] userName으로 사용자 정보를 로딩
    // 로그인 시 사용자 정보를 DB에서 조회하고 검증한 뒤,
    // Spring Security가 이해할 수 있는 형태로 래핑해서 반환하는 역할
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("[서비스] CustomerUserDetailsService - loadUserByUsername 메서드 실행"); // 디버깅

        // 0) 사용자 정보 조회 (userName)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 1) 사용자 정보 추출
        Long userId = user.getId();
        String userName = user.getUsername();
        String password = user.getPassword();

        // 2) 권한 리스트 생성 & 권한 추가
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER"); // 기본 권한 부여
        authorities.add(authority); // 권한 추가

        // 3) 사용자 정보와 권한을 담은 CustomUserDetails 객체 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(userId, userName, password, authorities);

        return customUserDetails;
    }
}
