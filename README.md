# social-login-project
[[인프런] 스프링 부트와 리액트로 구현하는 소셜 로그인](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-%EB%A6%AC%EC%95%A1%ED%8A%B8-%EC%86%8C%EC%85%9C%EB%A1%9C%EA%B7%B8%EC%9D%B8/dashboard)

<br>

### 9월 30일
1. [섹션 1, 2, 3] 개발 환경 준비 및 Spring Boot 프로젝트 생성
2. [섹션 4] 일반 로그인 코드 작성 및 **로그인 인증 처리 클래스** 정의
3. UserService에서 PasswordEncoder를 통해 사용자가 입력한 비밀번호를 DB에 저장할 때 암호화하여 저장하도록 하는 함수를 배움
4. CustomUserDetails 클래스에서 사용자 정보를 담는 클래스, 즉 로그인한 사용자 정보를 Spring Security가 이해할 수 있는 형태로 감싸주는 코드를 작성함
5. CustomUserDetailsService에서 로그인 시 사용자 정보를 DB에서 조회하고 검증한 뒤, Spring Security가 이해할 수 있는 형태로 래핑해서 반환하는 코드를 작성함

### 10월 1일
1. [섹션 4] 리스너(이벤트 발생 알아차림)와 핸들러(이벤트 발생 후 다음 동작 메서드)를 통해 로그인 성공 및 실패 시, 어떻게 처리할지에 대한 다음 동작을 구현함
2. 로그인 실패 시 `CustomLoginFailureHandler` 핸들러가 호출 되며 실패 로그를 남기고, 사용자가 접근하려던 `targetUrl`로 리다이렉트 시킴 (UX 측면에서 중요)
3. 로그인 성공 시 `CustomLoginSuccessHandler` 핸들러가 호출 되며 성공 로그를 남기고, 로그인 성공 후 이동할 기본 URL로 리다이렉트 시킴
4. `Config`에서 로그인, 로그아웃, URL 별 접근 권한, 패스워드 암호화 및 핸들러를 등록함
5. 정리 : 이벤트가 발생하면 리스너가 자동으로 반응하고(`@Component` 어노테이션 사용), `Config`에 등록된 핸들러가 해당 이벤트에 따른 다음 동작을 처리함

### 10월 3일
1. [섹션 4] 로그인, 로그아웃 등을 처리하는 `HomeController`, 할 일을 관리하는 `TodoController`, 사용자 등록, 로그인을 처리하는 `UserController`를 구현함

### 10월 14일
1. [섹션 4 19강] resources/templates/ 경로에 시작(`index.html`) / 로그인(`login.html`) / 회원가입(`register.html`) / 할 일 CRUD(`todos.html`) / 할 일 수정(`edit-todo.html`) 페이지 작성 함

### 10월 15일
1. Swagger UI에서 REST API를 확인하려면 Controller에서 반환 데이터가 JSON 형식 or 문자열이어야 함. html 페이지는 Swagger UI로 출력할 수 없음 … 다만 @RestController 어노테이션을 사용하면 Swagger UI에서 확인은 가능. 다만 html 페이지로 접속하면 문자열만 반환할 뿐 html 페이지를 보여주지 않음. 결론, 못 쓴 다 ! 내가 원한건 Swagger UI에서 api를 확인만 하는 거신데 … 아무튼 안된답니다
2. 기본 UI 수정 함 ㅎㅎ 나름 예뻐진듯
3. 예뻐지면 뭐함 로그인이 안 되는데 당최 왜 안되는 것인지 모르겠다 ㅎ 회원가입은 잘 됐는데 ,,, 가끔 강사님 코드대로 똑같이 실행해도 내꺼만 안되는 사태가 발생해서 제미나이에게 물어물어 가며 원인을 찾아내곤 하는데 ... 버전이 바뀌었나 ... 싶을 때가 종종 있다가도 제미나이 솔루션이 더 설득력 있어서 제미나이가 하자는 대로 따를 때가 더 많다.
4. 아무튼 회원가입 시 BCrypt 를 사용해 데이터베이스 상에 암호화된 패스워드가 저장되도록 구현해뒀는데, 그 부분까진 잘 진행된 듯 하다. 쩝
5. 내일 왜 로그인이 안되는지 원인을 다시 찾아봐야겠다. 대충 짐작 가는 점은 회원가입 시 저장된 패스워드...가 암호화 되는 문제가 아닐런지 ... 싶다
