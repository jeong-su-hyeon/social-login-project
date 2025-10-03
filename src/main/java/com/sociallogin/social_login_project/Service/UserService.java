package com.sociallogin.social_login_project.Service;

import com.sociallogin.social_login_project.Entity.User;
import com.sociallogin.social_login_project.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 사용자가 입력한 비밀번호를 암호화해서 저장

    // [추가] 회원가입
    public User registerUser(String userName, String password){

        // [중복확인] 동일한 username이 존재하는지 확인
        if(userRepository.findByUsername(userName).isPresent())
            throw new RuntimeException("이미 존재하는 사용자 이름입니다.");

        // 새로운 User 객체 생성
        User user = new User();
        user.setUserName(userName);
        user.setPassword(passwordEncoder.encode(password)); // 비밀번호를 암호화된 값으로 저장

        return userRepository.save(user); // DB에 User 객체 저장
    }

    // [조회] 로그인 및 사용자 정보 조회 시 사용
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
