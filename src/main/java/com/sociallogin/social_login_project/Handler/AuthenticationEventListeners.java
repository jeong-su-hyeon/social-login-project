package com.sociallogin.social_login_project.Handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Slf4j // 로깅 기능 활성화 (서버 로그에 기록해두고 싶을 때 유용)
@Component // 스프링이 관리하는 컴포넌트로 등록
public class AuthenticationEventListeners {

    // [리스너] 로그인 성공, 실패를 감지하여 원하는 동작을 실행할 수 있게 해줌

    // [로그인 인증 이벤트] (공통)
    // AbsractAuthenticationEvent : 로그인 성공, 실패 등 다양한 인증 관련 이벤트들의 상위 클래스
    // -> 인증에 대한 이벤트가 발생하면 그에 대한 관련 정보(event)가 들어오게 됨
    @EventListener
    public void handleAuthenticationEvent(AbstractAuthenticationEvent event) {
        log.info("[이벤트 리스너 - 로그인 인증] handleAuthenticationEvent\n" + event);
    }

    // [로그인 실패 이벤트]
    // ex. 실패 로그, 실패 횟수 증가, 일정 횟수 이상 실패 시 계정 잠금 처리 등
    @EventListener
    public void handleBadCredentials(AuthenticationFailureBadCredentialsEvent event) {
        log.info("[이벤트 리스너 - 로그인 실패] handleBadCredentials\n" + event);
    }

    // [로그인 성공 이벤트]
    // ex. 마지막 로그인 시간 기록, 성공적으로 로그인한 사용자에게 알림, 로그인 시도 횟수 초기화
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        log.info("[이벤트 리스너 - 로그인 성공] handleAuthenticationSuccess\n" + event);
    }
}
