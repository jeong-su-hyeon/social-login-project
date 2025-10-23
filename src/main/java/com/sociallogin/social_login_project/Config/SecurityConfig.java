package com.sociallogin.social_login_project.Config;

import com.sociallogin.social_login_project.Handler.LoginFailureHandler;
import com.sociallogin.social_login_project.Handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor    // final로 선언된 필드에 대해 자동으로 생성자를 만들어줌
@Configuration              // Spring 설정 클래스
@EnableWebSecurity          // Spring Security 웹 보안 활성화
// < Spring Security 설정 담당 >
// (인증, 인가, JWT 필터, OAuth 설정 등)
public class SecurityConfig {

    // [소셜 로그인 처리 서비스]
    // 소셜 로그인 시, 이 서비스가 호출되어 사용자 이메일을 읽음
    // DB에 등록하거나 기존 회원을 불러오는 역할
    private final OAuth2UserService customOAuth2UserService;

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
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler) throws Exception {
        http

        // 1) URL 별 접근 권한 설정
        .authorizeHttpRequests(auth -> auth
                // 누구나 접근 가능
                .requestMatchers("/css/**", "/js/**").permitAll()

                // Swagger UI 및 API 문서 경로 허용
                .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml").permitAll()

                // 홈페이지, 로그인, 회원가입 페이지 허용
                .requestMatchers("/", "/users/register", "/login").permitAll()

                // 파비콘 허용
                .requestMatchers("/favicon.ico").permitAll()

                // 그 외의 요청은 인증 필요
                .requestMatchers("/todos").authenticated()
                .anyRequest().authenticated()


        )
        // 2) 로그인 설정
//        .formLogin(form -> form
//                .loginPage("/login")                // 로그인 페이지 경로
//                .permitAll()                        // 로그인은 인증 없이 접근 가능
//                .defaultSuccessUrl("/todos", true) // 로그인 성공 시 리다이렉트 URL (무조건 todos)
//                .successHandler(authenticationSuccessHandler)     // 로그인 성공 시 핸들러
//                .failureHandler(authenticationFailureHandler)     // 로그인 실패 시 핸들러
//                //.userInfoEndPoint(userInfo -> userInfo.userService(customOAuth2UserService)
//        )
        // 2) OAuth2 로그인 설정
        .oauth2Login(oauth2 -> oauth2
            .loginPage("/login")
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
            .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService))
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
    // 핸들러를 따로 설정하는 이유
    // ex 로그인 성공 시 사용자 이름을 남기기, 마지막 로그인 시간 저장, 특정 페이지로 동적 이동할 수 있음
    // 로그인 실패 시, 실패 사유를 사용자에게 알려주거나, 로그인 실패 횟수를 누적해서 보안 조치를 취할 수도 있음
    // -> 이러한 커스터마이징을 핸들러를 통해 할 수 있음
    // 1) 로그인 성공 시
    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new LoginSuccessHandler();
    }

    // 2) 로그인 실패 시
    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        return new LoginFailureHandler();
    }
}
