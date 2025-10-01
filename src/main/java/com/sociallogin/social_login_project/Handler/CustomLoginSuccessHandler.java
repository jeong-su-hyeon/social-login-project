package com.sociallogin.social_login_project.Handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    // 로그인 성공 시 동작하는 Spring Security의 커스텀 로그인 성공 핸들러
    // 역할 1) Spring Security에서 로그인 성공 시 호출되는 콜백 구현
    // 역할 2) 로그인 성공 후 어떤 동작을 할 것인지 직접 정의

    // [메서드]
    // 로그인 성공 시 호출되는 메서드
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,         // 클라이언트 요청 객체
            HttpServletResponse response,       // 서버 응답 객체
            Authentication authentication       // 인증된 사용자 정보
    ) throws IOException, ServletException {

        // 1 [로그인 성공 로그]
        log.info("[핸들러 - 로그인 성공");         // 디버깅용 or 보안 모니터링용

        // 로그인 성공 후 이동할 기본 URL
        String targetUrl = "/";                 // 자유롭게 변경

        // 2 [해당 URL로 리다이렉트]
        response.sendRedirect(targetUrl);
    }
}
