package com.sociallogin.social_login_project.Service;

import com.sociallogin.social_login_project.Entity.Todo;
import com.sociallogin.social_login_project.Entity.User;
import com.sociallogin.social_login_project.Repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor    // final 필드를 매개변수로 저장하는 생성자 (자동 생성)
@Service                    // 서비스 계층 컴포넌트임을 명시
public class TodoService {

    private final TodoRepository todoRepository; // Todo DB에 접근하기 위한 레포지토리

    // [추가] 할 일에 사용자 정보를 설정하고, DB에 저장
    public Todo addTodo(Todo todo, User user) {
        todo.setUser(user);                 // 해당 할 일에 사용자 정보 설정
        return todoRepository.save(todo);   // DB에 저장 후 저장된 객체 반환
    }

    // [조회] 사용자 ID로 할 일 목록을 조회
    public Optional<Todo> getTodosByUser(User user) {
        return todoRepository.findByUserId(user.getId()); // user_id 기준으로 조회
    }

    // [조회] 할 일 ID로 할 일 정보를 조회
    public Optional<Todo> getTodoById(Long id) {
       return todoRepository.findById(id);
    }

    // [수정] 특정 ID의 할 일을 수정
    // 수정 & 삭제 시엔 사용자 본인만 자신의 할 일을 수정할 수 있도록 보안 처리 필수 !!
    public void updateTodo(Long id, String title, String description, User user) {

        // [예외처리 1] 사용자 ID로 할 일 조회, 없으면 예외
        Todo todo = todoRepository.findByUserId(id)
                    .orElseThrow(() -> new IllegalArgumentException("할 일을 찾을 수 없습니다."));

        // [예외처리 2] 현재 사용자와 할 일의 사용자가 일치하지 않으면 예외
        if(!todo.getUser().getId().equals(user.getId()))
            throw new SecurityException("사용자에게 할 일을 수정할 권한이 없습니다.");

        // 검증 통과 => 수정
        todo.setTitle(title);
    }

    // [삭제] 특정 ID의 할 일 삭제
    public void deleteTodoById(Long id, User user) {

        // [예외처리 1] ID로 할 일 조회, 없으면 예외
        Todo todo = todoRepository.findByUserId(id)
                .orElseThrow(()->new IllegalArgumentException("할 일을 찾을 수 없습니다."));

        // [예외처리 2] 현재 사용자와 할 일의 소유자가 일치하지 않으면 예외
        // todo 객체에 저장된 사용자 ID와 user 객체에 저장된 사용자 ID를 서로 비교한다.
        if(!todo.getUser().getId().equals(user.getId()))
            throw new SecurityException("사용자에게 할 일을 삭제할 권한이 없습니다.");

        // 검증 통과 => 삭제
        todoRepository.deleteById(id);
    }
}
