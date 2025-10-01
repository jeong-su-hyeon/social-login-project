package com.sociallogin.social_login_project.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
// Spring Security에서 사용자 정보를 담는 커스텀 UserDetails 클래스
// 로그인한 사용자 정보를 Spring Security가 이해할 수 있는 형태로 감싸주는 중간 다리 역할
public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L; // 직렬화를 위한 고유 식별자 (예: 세션 저장 or 캐싱 기능)
    private final Long userId;
    private final String userName;
    private final String password;

    // [ 사용자 권한 목록 관련 ]
    // Spring Security가 인가 처리할 때 핵심적으로 사용함
    private final Collection<? extends GrantedAuthority> authorities;
    // ex) 사용자 - ROLL_USER, 관리자 - ROLL ADMIN 이런식으로 저장 됨

    // [ CustomUserDetails 클래스에서 반드시 구현 ] (@Override)
    // [Get] 사용자 권한 정보 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {  return authorities;  }

    // [Get] 사용자 비밀번호 반환
    // 사용자가 입력한 비밀번호와 DB에 저장된 암호화된 비밀번호를 비교할 때 사용함
    @Override
    public String getPassword() { return password; }

    // [Get] 사용자 이름 반환
    @Override
    public String getUsername() { return userName; }

    // [Get] 사용자 ID 반환 (Custom)
    public Long getUserId() { return userId; }
}
