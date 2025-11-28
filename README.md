# social-login-project
[[인프런] 스프링 부트와 리액트로 구현하는 소셜 로그인(1-47강)](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-%EB%A6%AC%EC%95%A1%ED%8A%B8-%EC%86%8C%EC%85%9C%EB%A1%9C%EA%B7%B8%EC%9D%B8/dashboard)

<br>

## 📂 프로젝트 개요
- Spring Boot와 Spring Security를 사용해 일반 로그인 및 OAuth2 소셜 로그인을 통합 구현한 백엔드 어플리케이션
- `Java 21`, `Spring Boot 3.5.6` 등을 사용했으며, 의존성은 다음 이미지와 같다.
- 기본 접속 경로 : `http://localhost:8080/` 
<img width="1061" height="656" alt="image" src="https://github.com/user-attachments/assets/1c846c38-f05f-45f7-a45d-8d728a756867" />



<br>

## 📂 데이터베이스 구성
```
USE sociallogin_db;

SHOW TABLES;
SELECT * FROM users;
SELECT * FROM todos;
DROP TABLE users;
DROP TABLE todos;

CREATE TABLE users (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    social_type VARCHAR(255),
    social_id VARCHAR(25)
);

CREATE TABLE todos (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    completed BOOL NOT NULL DEFAULT FALSE,    
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) 
		ON DELETE CASCADE
);
```

<br> 

## 📂 파일 구성
### `Config`
- `SecurityConfig` : Spring Security 설정 담당 (인증, 인가, JWT 필터, OAuth2 설정 등)
### `Controller`
- HTTP의 요청을 받고 응답을 반환 
- `HomeController` : 로그인, 로그아웃, 메인페이지 접속 요청 처리
- `UserController`: 회원가입 요청 처리
  
  → `UserController`에는 회원 추가 로직만 있고, 로그인 로직이 없다. Spring Securiyt가 인증 과정을 담당하기 때문에 Spring Security가 인증 요청을 가로채서 로그인 인증 과정을 시작한다. `UserController`에서는 단순히 회원 정보를 데이터베이스에 저장하는 가입 로직만 구현되어 있다. 로그인 확인 및 세션 발급 과정은 Spring Security의 몫.
- `TodoController` : 할 일 목록 CRUD 요청 처리
### `Service` 
- 핵심 비즈니스 로직 처리
- `UserService` : 회원가입, 로그인 로직
  
	→ `UserController`에서 회원 추가 로직만 작성했는데, `UserService`에서 `findByEmail`로 회원 조회 메서드를 작성한 이유는 Spring Security가 인증을 완료하기 위함이다. 로그인 처리는 Spring Security가 처리하지만, 인증에 필요한 데이터베이스 상의 회원 정보 비교는 `UserService`를 통해 Spring Security의 `PasswordEncoder`가 처리한다.
- `TodoService` : 할 일 목록 CRUD 로직 
### `Repository`
- 데이터베이스에 데이터를 저장, 조회, 수정, 삭제하는 CRUD 작업을 수행한다.
- `UserRepository` : `findByEmail()` 메서드로 사용자 이름으로 사용자 정보를 조회한다.
- `TodoRepository` : `findByUserId()` 메서드로 사용자 ID 에해당하는 할 일 목록을 조회한다.
### `Entity`
- 데이터베이스의 테이블을 연결해주는 클래스
- `User` 
- `Todo` 
### `DTO`
- `OAuth2Attributes` : 로그인 성공 후 인증 제공자(구글, 네이버, 카카오, 깃허브 등)가 제공하는 사용자 정보를 Map 형태로 전송하고, 해당 정보를 파싱을 통해 우리가 사용할 정보로 바꾸는 과정을 수행한다.
- 플랫폼마다 반환하는 사용자 정보 JSON의 구조가 다 다르기 때문에 팩토리 메서드 패턴으로 구현하여 로그인 플랫폼에 따라 분기 처리하도록 했다.
### `Handler`
- 로그인 이벤트 발생 시, 어떻게 처리할지 구현했다.
- `AuthenticationEventList`
- `LoginSucessHandler` : 로그인 성공 시 처리 로직 (로그인 성공 로그, 리다이렉트할 URL 등)
- `LoginFailureHandler` : 로그인 실패 시 처리 로직 (로그인 실패 로그, 실패 원인, 리다이렉트할 URL 등)
### `Security`
- `CustomUserDetailsService` : `loadByUsername()` 메서드로 사용자가 일반 로그인을 진행했을 때의 로직을 담당한다.
- `CustomUserDetails` : 로그인한 사용자 정보를 저장하는 객체 클래스
- `CustomOAuth2UserService` : `loadUser()` 메서드로 사용자가 소셜 로그인을 진행했을 때의 로직을 담당함. 사용자 정보 조회 및 플랫폼을 구분하고, `OAuth2Attributes` 클래스의 분기 메서드를 통해 사용자 정보를 매핑한다. 사용자 정보를 추출해 사용자에게 권한을 부여하고, 데이터베이스를 조회해 신규 회원이면 회원가입 처리, 기존에 있던 회원이면 로그인 처리한다.
- `CustomOAuth2User`: `CustomOAuth2UserService`에서 처리한 데이터를 통해 인증 객체를 생성 및 반환하며, 이 클래스를 통해 Service에서 현재 인증된 사용자의 정보를 활용할 수 있다.
- `CustomUser` : 일반 로그인과 소셜 로그인 모두 처리할 수 있는 사용자 인증 객체로 `UserDetails`와 `OAuth2User` 클래스를 `implements` 로 동시에 구현한다. (현재는 `CustomUser` 클래스로 현재 인증된 사용자 정보를 활용한다.)

<br>

## ✅ 일반 로그인 흐름
🔗 [일반 로그인 실행 영상](https://drive.google.com/file/d/1a3ivPc1l57IT7k3O9no1CvNTFn9Pv_5Y/view?usp=drive_link)

1. 사용자가 `/login` 경로로 페이지 접속    
    → `HomeController.java` 에서 `login()` 메서드 실행     
    → `return login;` 으로 `login.html`  페이지 렌더링    
2. login 페이지 입장(?)    
    `<intput>` 태그에 사용자 이름과 비밀번호를 입력     
    → 태그에 입력된 내용은 `POST /login` 으로 요청된다.    
    → 이 요청은 Sprig Security의 내부 필터인 `UsernamePasswordAuthenticationFilter` 가 로그인 처리를 진행함    
3. Spring Security 가 로그인을 처리    
    → 보통 로그인 처리 로직을 직접 작성하지 않아도  됨    
    대신 Spring Security 설정을 통해 로그인 흐름을 커스터마이징 함    
    →  로그인할 페이지 경로, 로그인 성공 시 리다이렉트할 URL 등등     
4. 사용자 입력값으로 사용자 정보 조회    
    `CustomUserDetailsService` 의 `loadUserByUsername()` 메서드를 통해 사용자 정보를 조회함    
    → 이때 조회된 정보를 `UserDetails` 형태로 반환    
5. 사용자 정보를 `UserDetails` 객체로 전달    
    ```java
    CustomUser customUser = new CustomUser(id, email, password, authorities);
    ```    
6. 비밀번호 검증    
    회원가입 시 비밀번호는 BCrypt로 암호화해서 저장    
    로그인 시에도 입력한 비밀번호를 BCrypt로 비교해서 일치 여부를 확인    
    ```java
    @Bean
    PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
    ```    
7. 로그인 성공 시 처리     
    로그인에 성공하면 `AuthenticationSuccessHandler` 핸들러 호출    
    ```java
    public class LoginSuccessHandler implements AuthenticationSuccessHandler {
        @Override
        **public void onAuthenticationSuccess(
                HttpServletRequest request,         // 클라이언트 요청 객체
                HttpServletResponse response,       // 서버 응답 객체
                Authentication authentication       // 인증된 사용자 정보
        ) throws IOException, ServletException {
            String targetUrl = "/todos";     
            response.sendRedirect(targetUrl);
        }
    }
    ```    
    → 여기서 로그인 성공 시 리다이렉트할 경로를 지정해도 되지만, Spring Security에서 .defaultSuccessUrl을 직접 설정해두면 해당 경로로 리다이렉트 됨
    
8. 로그인 실패 시 처리    
    로그인에 실패하면 `AutehnticationFailureHandler` 핸들러 호출    
    ```java
    public class LoginFailureHandler implements AuthenticationFailureHandler {
    
        private RequestCache requestCache = new HttpSessionRequestCache();
        @Override
                public void onAuthenticationFailure(
                        HttpServletRequest request,         // 클라이언트 요청 객체
                        HttpServletResponse response,       // 서버 응답 객체
                        AuthenticationException exception   // 로그인 실패 정보 (원인)
        ) throws IOException, ServletException {
    
            // 1 [로그인 실패 예외 로그]
            // 실패 원인을 로그로 남긴다.
            log.info("\n[로그인 실패 핸들러 - 실패 원인] onAuthenticationFailure Exception\n-> " + exception);
    
            // 사용자가 접근하려던 요청 정보 가져오기
            SavedRequest savedRequest = requestCache.getRequest(request, response);
    
            // 2 [사용자가 접근하려던 URL로 리다이렉트]
            if(savedRequest != null) {
                // 2-1 사용자가 접근하려던 URL
                String targetUrl = savedRequest.getRedirectUrl();
    
                // 2-2 실패 시 사용자가 접근하려던 URL 로그 출력
                log.info("\n[로그인 실패 핸들러 - 사용자가 접근 시도한 URL]\n->" + targetUrl);
    
                // 2-3 targetUrl로 리다이렉트
                // -> 로그인에 실패했지만 사용자를 원래 있던 페이지로 보내줌
                response.sendRedirect(targetUrl);
            }
            // 3 [사용자가 접근하려던 요청 정보가 없는 경우]
            else {
                log.info("\n[핸들러 - 사용자가 접근하려던 요청 정보가 없음]");
                // 3-1 에러 메시지를 세션에 담아서 로그인 페이지로 전달
                request.getSession().setAttribute("loginError", "로그인 에러 발생!!");
    
                // 3-2 로그인 페이지로 리다이렉트
                response.sendRedirect("/login?error");
            }
        }
    }
    
    ```    
    → 실패 로그를 기록, `/login?error` 페이지로 리다이렉트 됨    
9. 로그인 후 사용자 정보 사용    
    로그인이 완료되면 인증된 사용자 정보를 페이지의 여러 곳에서 사용할 수 있음    
    ex) `TodoController.java` 
    
<br>

## ✅ 소셜 로그인 흐름
🔗 [네이버 로그인 실행 영상](https://drive.google.com/file/d/19SvoAknuE2MiX-nv63u2BgtdHfn9efmM/view?usp=drive_link)
1. 소셜 로그인 버튼 클릭    
    → 사용자가 로그인 버튼을 클릭하면, Spring Security가 자동으로 해당 플랫폼의 인증 페이지로 리디렉션      
    ```html
    <!-- [영역 4] 소셜 로그인 영역 -->
    <div class="center-align">
    	<p>Or login with</p>
    	<!-- [버튼] 구글 로그인 링크 -->
    	<!-- 별도의 컨트롤러 없이도 해당 url을 Spring Security가 처리해 줌 -->
    	<a href="/oauth2/authorization/google" class="btn waves-effect waves-light red">Google</a>
    	<!-- [버튼] 네이버 로그인 링크 -->
    	<a href="/oauth2/authorization/naver" class="btn waves-effect waves-light green">Naver</a>
    	<!-- [버튼] 카카오 로그인 링크 -->
    	<a href="/oauth2/authorization/kakao" class="btn waves-effect waves-light yellow black-text">Kakao</a>
    	<!-- [버튼] 깃허브 로그인 링크 -->
    	<a href="/oauth2/authorization/github" class="btn waves-effect waves-light black">Github</a>
    </div>
    ```    
2. OAuth 인증 완료 후 콜백 처리     
    → 사용자가 인증을 마치면 OAuth2 프로바이더는 특정 경로로 콜백을 보냄    
    → `/login/oauth2/code/{provider}` 형태    
    → Spring Security의 설정을 통해 인증 완료 후 사용자 정보를 가져옴    
3. 사용자 정보 매핑 및 저장     
    `CustomOAuth2UserService`     
    <aside>
    
    ① 프로바이더로부터 사용자 정보를 수신
    ② 어떤 플랫폼인지 구분 
    ③ 사용자 정보 매핑
    ④ 이메일 기준을 사용자 조회
    ⑤ 신규 사용자면 DB에 저장, 기존 사용자면 그대로 사용
    ⑥ 로그인 세션 정보 생성
    
    </aside>
    
    ```java
    return new CustomUser(userId, email, authorities, attributes);
    ```
    
    → 플랫폼마다 제공하는 사용자 정보 형태가 조금씩 다름     
    → `OAuth2Attribtes` 클래스에서 팩토리 메서드 패턴으로 플랫폼 별로 각각 다르게 매핑하도록 구현해둠     
4. 인증 성공/실패 처리     
    일반 로그인 과정과 동일     
5. 인증된 사용자 정보 사용     
    일반 로그인 과정과 동일     
    → 각 소셜 로그인에 필요한 클라이언트 정보는 `application.properties` 에 설정
