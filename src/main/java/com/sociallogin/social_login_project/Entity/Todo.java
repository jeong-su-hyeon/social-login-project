package com.sociallogin.social_login_project.Entity;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="todos")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 (AUTO INCREMENT)
    private Long id;
    private String title;
    private String description;
    private boolean completed;

    // 다:1 관계 (여러 개의 할 일 <-> 하나의 사용자)
    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;
}
