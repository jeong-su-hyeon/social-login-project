package com.sociallogin.social_login_project.Controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Spring MVC 기반 컨트롤러

    // @GetMapping : GET 요청 처리
    @GetMapping("/")
    public String home() {
        return "index";  // "index"이라는 이름의 뷰(템플릿)를 반환 (resources/templates/index.html 을 찾아 출력)
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";    // 로그아웃 후 "/login?logout" 경로로 리다이렉트
                                            // ? : 로그아웃이 성공적으로 완료되었다는 의미
                                            // 3:19초
    }
}
