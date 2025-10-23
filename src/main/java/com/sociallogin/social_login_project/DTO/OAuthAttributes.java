package com.sociallogin.social_login_project.DTO;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
// OAuth 인증 후 반환된 사용자 정보를 담는 DTO 클래스
public class OAuthAttributes {
    // [멤버 필드]
    private Map<String, Object> attributes; // OAuth로부터 전달받은 사용자 정보 전체 Map
    private String nameAttributeKey;        // OAuth 사용자 식별 키 (Map의 key 부분)
    private String name;                    // 사용자 이름
    private String email;                   // 사용자 이메일
    private String picture;                 // 사용자 프로필 사진
    private String id;                      // 사용자 고유 ID (Map을 다루기 위한 외부에서 주입된 정보)

    // [생성자] Builder 패턴
    @Builder
    public OAuthAttributes(
            Map<String, Object> attributes,
            String nameAttributeKey,
            String name,
            String email,
            String picture,
            String id
    ) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.id = id;
    }

    // [생성자] 팩토리 메서드
    // 현재는 Google만 처리하며, 추후 다른 플랫폼에 따라 분기 가능
    // OAuthAttributes 객체를 생성하는 정적 팩토리 메서드
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeName, attributes); // Google 로그인 처리
    }

    // [메서드]
    // Google 로그인 전용 사용자 정보 매핑 메서드
    // -> Google에서 받은 Map<> 에서 추출된 정보를 담음
    // -> Map에서 받은 정보들을 key를 이용해 필요한 값들을 하난씩 추출 -> String 타입으로 저장 (멤버필드 변수)
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))              // 사용자 이름 매핑
                .email((String) attributes.get("email"))            // 사용자 이메일 매핑
                .picture((String) attributes.get("picture"))        // 사용자 프로필 사진 매핑
                .id((String) attributes.get(userNameAttributeName)) // 사용자 ID 매핑
                .attributes(attributes)                             // Map<> 자체 저장
                .nameAttributeKey(userNameAttributeName)            // 식별 키 저장
                .build();                                           // 빌더로 객체 생성
    }
}
