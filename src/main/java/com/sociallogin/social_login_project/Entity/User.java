package com.sociallogin.social_login_project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 (AUTO INCREMENT)
    private Long id;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "social_type")   // 소셜 로그인 유형 (Goolge, Naver ...)
    private String socialType;

    @Column(name = "social_id")     // 소셜 로그인 ID (외부에서 제공 받은 사용자 식별자)
    private String socialId;

    // 1:다 관계 (사용자 <-> 할일)
    // Todo 엔티티의 user 필드를 기준으로 관계 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Todo> todos; // Set 자료형 : 할 일이 중복 없이 저장되게 하기 위함 (List 자료형 대신 Set을 사용함)
}
