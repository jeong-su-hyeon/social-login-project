package com.sociallogin.social_login_project.Config;

import com.sociallogin.social_login_project.Handler.CustomLoginFailureHandler;
import com.sociallogin.social_login_project.Handler.CustomLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration      // Spring의 환경 구성 요소로 인식 (.xml 이런거...)
@EnableWebSecurity  // Spring Security 웹 보안 활성화
public class SecurityConfig {

    // Spring Security 설정 담당 (인증, 인가, JWT 필터, OAuth 설정 등)

    // [비밀번호 암호화 처리]
    @Bean        //
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

        // BCrypte (기본값, 권장 알고리즘)
        // - 단방향) 복호화(암호화된 값 -> 원래 비밀번호)가 불가능하기 때문에 안전함
        // - Salt) - 같은 비밀번호라도 매번 암호화 결과가 달라져서 보안성이 높음
    }

    // [보안 설정 핵심 구성 요소]
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

        // 1) URL 별 접근 권한 설정
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/users/register", "/login", "/css/**", "/js/**").permitAll() // 누구나 접근 가능
                .anyRequest().authenticated()       // 그 외의 요청은 인증 필요
        )
        // 2) 로그인 설정
        .formLogin(form -> form
                .loginPage("/login")                // 로그인 페이지 경로
                .permitAll()                        // 로그인은 인증 없이 접근 가능
                .defaultSuccessUrl("/todos", true) // 로그인 성공 시 리다이렉트 URL (무조건 todos)
        )
        // 3) 로그아웃 설정
        .logout(logout -> logout
                .logoutUrl("/logout")               // 로그아웃 페이지 경로
                .logoutSuccessUrl("/login?logout")  // 로그아웃 성공 시 이동할 URL
                .permitAll()                        // 로그아웃은 인증 없이 접근 가능
        );

        return http.build();                        // 설정 완료 후 SecurityFilterChain 반환
    }

    // [핸들러 등록]
    // 1) 로그인 성공 시
    @Bean
    AuthenticationSuccessHandler authenticationSuccessHadnler() {
        return new CustomLoginSuccessHandler();
    }

    // 2) 로그인 실패 시
    @Bean
    AuthenticationFailureHandler authenticationFailureHadnler() {
        return new CustomLoginFailureHandler();
    }
}
