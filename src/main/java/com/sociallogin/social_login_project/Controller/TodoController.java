package com.sociallogin.social_login_project.Controller;

import com.sociallogin.social_login_project.Entity.Todo;
import com.sociallogin.social_login_project.Entity.User;
import com.sociallogin.social_login_project.Security.CustomUserDetails;
import com.sociallogin.social_login_project.Service.TodoService;
import com.sociallogin.social_login_project.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/todos/") // 엔드포인트의 맨 앞 고정 경로
@RequiredArgsConstructor
public class TodoController {

    // 사용자의 할 일 조회, 추가, 수정, 삭제 기능 처리

    private final TodoService todoService;
    private final UserService userService;

    // [조회 1] 인증된 사용자의 할 일 목록 조회
    // 사용자가 로그인 해야만 볼 수 있는 개인화된 페이지 "todos"
    @GetMapping
    public String getTodos(Authentication authentication, Model model) {
        // 1 [현재 인증된 사용자 인증 정보 받기]
        Object principal = authentication.getPrincipal();

        // (인증 정보 X)
        if(principal == null)           // 인증 정보가 없으면
            return "redirect:/login";   // 로그인 페이지로 리다이렉트

        // (인증 정보 O)
        CustomUserDetails customUserDetails = (CustomUserDetails) principal;                // 사용자 정보를 커스텀 객체로 캐스팅

        // 2 [사용자 정보를 DB 에서 조회]
        Optional<User> user = userService.findByUsername(customUserDetails.getUsername());

        // (사용자 정보 X)
        if(user.isEmpty())              // 사용자 정보가 없으면
            return "redirect:/login";   // 로그인 페이지로 리다이렉트

        // (사용자 정보 O)
        // 3 [해당 사용자에 대한 할 일 목록 조회]
        List<Todo> todosList = todoService.getTodosByUserId(user.get());

        // 4 [ 데이터를 웹 페이지(html)로 전달 ]
        model.addAttribute("todos", todosList); // 위에서 처리한 데이터를 웹페이지(html)로 전달

        return "todos"; // "todos.html" 뷰 반환
    }

    // [추가] 사용자의 할 일 추가
    // @ModelAttribute : HTML 폼을 통해 서버로 전송된 데이터를 객체에 자동으로 담아줌
    @PostMapping("/add")
    public String addTodo(Authentication authentication, @ModelAttribute Todo todo) {

        // 1 [현재 인증된 사용자 정보 받기]
        Object principal = authentication.getPrincipal();
        if(principal == null)
            return "redirect:/login";
        CustomUserDetails customUserDetails = (CustomUserDetails) principal;

        // 2 [사용자의 할 일 추가]
        User user = new User();
        user.setId(customUserDetails.getUserId());  // 현재 로그인한 사용자 ID
        todoService.addTodo(todo, user);            // 추가

        // 3 [할 일 목록으로 리다이렉트]
        return "redirect:/todos";
    }

    // [삭제] 사용자의 할 일 삭제
    @PostMapping("/delete/{id}")
    public String deleteTodo(@PathVariable("id") Long id, Authentication authentication) {
        // 1 [현재 인증된 사용자 정보 받기]
        Object principal = authentication.getPrincipal();
        if (principal == null)
            return "redirect:/login";
        CustomUserDetails customUserDetails = (CustomUserDetails) principal;

        // 2 [사용자의 할 일 목록 삭제]
        User user = new User();
        user.setId(customUserDetails.getUserId());  // 현재 사용자 ID
        todoService.deleteTodoById(id, user);       // 삭제

        return "redirect:/todos";
    }

    // [조회 2] 할 일 수정을 위한 데이터 조회
    @GetMapping("/edit/{id}")
    public String editTodo(@PathVariable("id") Long id, Model model, Authentication authentication) {
        // 1 [현재 인증된 사용자 정보 받기]
        Object principal = authentication.getPrincipal();
        if(principal == null)
            return "redirect:/login";
        CustomUserDetails customUserDetails = (CustomUserDetails) principal;

        // 2 [현재 사용자 할 일 조회]
        Long userId = customUserDetails.getUserId(); // 현재 사용자 ID
        Optional<Todo> todoOptional = todoService.getTodoById(id); // 해당 사용자의 할 일 조회

        // 3 [해당 할 일이 사용자 소유인지 확인]
        if(todoOptional.isPresent() && todoOptional.get().getUser().getId().equals(userId)) {
            model.addAttribute("todo", todoOptional.get());
            return "edit_todo"; // 수정 폼 페이지 반환
        }

        return "redirect:todos"; // 사용자 소유가 아닐 시, 할 일 목록으로 ...
    }


    // [수정] 사용자의 할 일 수정
    @PostMapping("/update/{id}")
    public String updateTodo(
            @PathVariable("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            Authentication authentication) {

        // 1 [현재 인증된 사용자 정보 받기]
        Object principal = authentication.getPrincipal();
        if (principal == null)
            return "redirect:/login";
        CustomUserDetails customUserDetails = (CustomUserDetails) principal;

        // 2 [사용자의 할 일 수정]
        User user = new User();
        user.setId(customUserDetails.getUserId()); // 현재 사용자 ID
        todoService.updateTodo(id, title,description, user);

        return "redirect:/todos";
    }
}
