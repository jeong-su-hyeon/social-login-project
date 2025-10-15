package com.sociallogin.social_login_project.Repository;

import com.sociallogin.social_login_project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// JpaRepository < 대상 엔티티, 기본키의 자료형 >
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // [쿼리 메서드]
    // [조회] 사용자 이름으로 사용자 정보 조회
    // Optional : 사용자를 찾지 못 했을 대 null이 나오는 것을 대비하여 null 대신 Optional.empty()를 반환하도록 함
    Optional<User> findByEmail(String username); // == SELECT * FROM users WHERE username = ?;
}
