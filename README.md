# social-login-project
[[인프런] 스프링 부트와 리액트로 구현하는 소셜 로그인](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-%EB%A6%AC%EC%95%A1%ED%8A%B8-%EC%86%8C%EC%85%9C%EB%A1%9C%EA%B7%B8%EC%9D%B8/dashboard)

<br>

### 프로젝트 개요
- Spring Boot와 Spring Security를 사용해 일반 로그인 및 OAuth2 소셜 로그인을 통합 구현한 백엔드 어플리케이션
- `Java 21`, `Spring Boot 3.5.6` 등을 사용했으며, 의존성은 다음 이미지와 같다.
<img width="1061" height="656" alt="image" src="https://github.com/user-attachments/assets/1c846c38-f05f-45f7-a45d-8d728a756867" />

기본 경로 : `http://localhost:8080/` 


### 데이터베이스 구성
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

## 📂 파일 구성
### `config`
- `Config` - `SecurityConfig` : Spring Security 설정 담당 (인증, 인가, JWT 필터, OAuth2 설정 등)
- `Controller` - `HomeController`(로그인, 로그아웃, 메인페이지 접속 처리), `TodoController`(할 일 목록 CRUD), `UserController`(회원가입, 로그인 등)
- `Service` 
`DTO`
`Entity` 
`Handler` 
`Repository` 
`Security`


