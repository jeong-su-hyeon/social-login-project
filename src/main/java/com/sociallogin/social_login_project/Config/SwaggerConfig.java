package com.sociallogin.social_login_project.Config;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@OpenAPIDefinition(
        info = @Info(
                title = "Social Login Project",
                description = "React와 Spring Security 기반으로 일반 로그인과 소셜 로그인 방법을 배워 카카오, 네이버, 구글 등으로 로그인할 수 있는 기능을 구현",
                version = "1.0.0"
        )
)
@Configuration
public class SwaggerConfig {
}
