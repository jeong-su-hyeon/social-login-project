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
    private String userName;
    private String password;

    // 1:다 관계 (사용자 <-> 할일)
    // Todo 엔티티의 user 필드를 기준으로 관계 설정
    @OneToMany(mappedBy = "user")
    private Set<Todo> todos; // Set 자료형 : 할 일이 중복 없이 저장되게 하기 위함 (List 자료형 대신 Set을 사용함)
}
