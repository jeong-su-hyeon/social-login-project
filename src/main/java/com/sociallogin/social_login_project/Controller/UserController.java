package com.sociallogin.social_login_project.Controller;

import com.sociallogin.social_login_project.Entity.User;
import com.sociallogin.social_login_project.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    // 사용자의 회원가입, 로그인 처리
    // 1) 회원가입 폼 보여주기 -> 2) 검증 및 저장 -> 3) 로그인 페이지 이동

    private final UserService userService;

    // [조회] 회원 가입 폼 보기
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // 새 User 객체를 모데렝 추가해 폼에서 바인딩하도록
        model.addAttribute("user", new User());
        return "register"; // "register.html" 반환
    }

    // [추가] 회원 등록
    // @ModelAttribute : HTML 폼을 통해 서버로 전송된 데이터를 객체에 자동으로 담아줌
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") User user,
            BindingResult result,
            Model model
    ) {
        //  1 [이메일 유효성 검사]
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";  // 이메일 정규 표현식
        if (!Pattern.matches(emailPattern, user.getUserName())) { // 이메일이 정규식과 일치 X
            result.rejectValue("userName", "error.user", "유효하지 않은 이메일 형식입니다. 유효한 이메일 주소를 입력해주세요."); // BindingResult 에 에러 등록
            model.addAttribute("emailError", "유효하지 않은 이메일 형식입니다. 유효한 이메일 주소를 입력해주세요."); // 뷰에 표시할 에러 메시지
        }

        // 2 [중복된 사용자 확인]
        if(result.hasErrors()) { // 중복 사용자라면
            return "register"; // 다시 등록 페이지로 이동
        }

        // 3 [중복되는 이메일 명 확인]
        if(userService.findByUsername(user.getUserName()).isPresent()) { // 중복되는 이메일이라면
            model.addAttribute("duplicatedError", "이미 존재하는 사용자 이름입니다."); // 에러 등록
            return "register";
        }

        // 4 [사용자 등록]
        userService.registerUser(user.getUserName(), user.getPassword());

        return "register:/login";
    }
}
