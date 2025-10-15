package com.sociallogin.social_login_project.Entity;

import jakarta.persistence.*;
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

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    // [다:1 관계] 여러 개의 TO-DO <-> 하나의 User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    // 편의 메서드 (??)
//    public void setUser(User user) {
//        this.user = user;
//    }
}
