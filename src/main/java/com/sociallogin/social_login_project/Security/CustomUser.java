package com.sociallogin.social_login_project.Security;

import com.sociallogin.social_login_project.DTO.OAuthAttributes;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

// 일반 로그인과 OAuth2 로그인을 모두 처리할 수 있는 사용자 인증 객체
// -> UserDetails, OAuth2User을 동시에 구현
// UserDetails : 일반 로그인 사용자의 필수 정보 제공
// OAuth2User : 소셜 로그인 사용자의 필수 정보 제공
@Getter
public class CustomUser implements UserDetails, OAuth2User {

    // [멤버 필드]
    private Long userId;                                            // 사용자 고유 ID (DB상)
    private String username;                                        // 사용자명 (로그인 ID or Email)
    private String password;                                        // 사용자 패스워드 (일반 로그인용)
    private Collection<? extends GrantedAuthority> authorites;      // 사용자 권한 목록
    private Map<String, Object> attributes;                         // 사용자 정보 Map (소셜 로그인용)

    // [생성자]
    // 일반 로그인 전용
    public CustomUser(Long userId, String username, String password, Collection<?extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorites = authorities;
        this.attributes = null;                             // 일반 로그인에는 OAuth2 속성이 필요 없음
    }

    // 소셜 로그인 전용
    public CustomUser(Long userId, String username, Collection<? extends GrantedAuthority> authorities, OAuthAttributes oAuthAttributes) {
        this.userId = userId;
        this.username = username;
        this.authorites = authorities;
        this.attributes = oAuthAttributes.getAttributes();  // OAuth2Attributes 에서 파싱한 값 저장
        this.password = null;                               // 소셜 로그인에는 패스워드가 필요 없음
    }

    // [재정의]
    // [Getter - 속성 값 반환]
    // OAuth2User에서 사용하는 고유 사용자명 반환 (Spring Security 용)
    @Override
    public String getName() {
        return username;
    }

    // OAuth2 사용자 정보 Map
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 사용자명 (로그인 ID or Email)
    @Override
    public String getUsername() {
        return username;
    }

    // 패스워드
    @Override
    public String getPassword() {
        return password;
        // 일반 로그인 -> 패스워드 반환
        // 소셜 로그인 -> null 반환
    }

    // 권한 리스트 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorites;
    }

    // [Getter 상태 반환]
    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;    // -> 계정이 만료되지 않음
    }

    // 계정 잠김 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;    // -> 계정이 잠겨있지 않음
    }

    // 자격 증명 유효
    @Override
    public boolean isCredentialsNonExpired() {
        return true;    // -> 자격 증명이 만료되지 않음
    }

    // 계정 활성화
    @Override
    public boolean isEnabled() {
        return true;    // -> 계정이 활성화되어 있음
    }

    // 사용자 ID 반환
    public Long getUserId() {
        return userId;
    }

}
