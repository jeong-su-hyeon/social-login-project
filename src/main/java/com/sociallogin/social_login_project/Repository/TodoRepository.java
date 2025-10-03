package com.sociallogin.social_login_project.Repository;

import com.sociallogin.social_login_project.Entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// JpaRepository - DB 저장/조회시 사용하는 DAO 역할의 클래스
// JpaRepository < 대상 엔티티, 기본키의 자료형 >
public interface TodoRepository extends JpaRepository<Todo, Long> {

    // [쿼리 메서드]
    // [조회] 사용자 ID에 해당하는 할일 목록 조회
    List<Todo> findByUserId(Long id); // == SELECT * FROM todos WHERE user_id = ?;
}
