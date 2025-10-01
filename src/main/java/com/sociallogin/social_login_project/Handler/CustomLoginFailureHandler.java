package com.sociallogin.social_login_project.Handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

@Slf4j
public class CustomLoginFailureHandler  implements AuthenticationFailureHandler {

    // 로그인 실패 시 동작하는 Spring Security의 커스텀 로그인 실패 핸들러
    // -> 실패 원인 로깅, 실패 후 리다이랙션 처리

    // [필드]
    // 로그인 시도 전 사용자가 요청한 URL을 저장
    // 로그인에 실패하더라도 원래 있던 URL 페이지로 다시 보내줄 수 있도록 저장하는 값 (requestCachce)
    private RequestCache requestCache = new HttpSessionRequestCache();

    // [메서드]
    // 로그인 실패 시 호출되는 메서드
    @Override
            public void onAuthenticationFailure(
                    HttpServletRequest request,         // 클라이언트 요청 객체
                    HttpServletResponse response,       // 서버 응답 객체
                    AuthenticationException exception   // 로그인 실패 정보 (원인)
    ) throws IOException, ServletException {

        // 1 [로그인 실패 예외 로그]
        // 실패 원인을 로그로 남긴다.
        log.info("[핸들러 - 로그인 실패 원인] onAuthenticationFailure Exception\n-> " + exception);

        // 사용자가 접근하려던 요청 정보 가져오기
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        // 2 [사용자가 접근하려던 URL로 리다이렉트]
        if(savedRequest != null) {
            // 2-1 사용자가 접근하려던 URL
            String targetUrl = savedRequest.getRedirectUrl();

            // 2-2 실패 시 사용자가 접근하려던 URL 로그 출력
            log.info("[핸들러 - 사용자가 접근 시도한 URL]\n-> " + targetUrl);

            // 2-3 targetUrl로 리다이렉트
            // -> 로그인에 실패했지만 사용자를 원래 있던 페이지로 보내줌
            response.sendRedirect(targetUrl);
        }
    }
}
