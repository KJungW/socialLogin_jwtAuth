package com.example.demo.entity;

import com.example.demo.type.LoginType;
import com.example.demo.type.UserRoleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Builder
    public User(LoginType loginType, String oauthUserId, String name, String email, UserRoleType role) {
        this.loginType = loginType;
        this.oauthUserId = oauthUserId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LoginType loginType; // oauth 로그인 타입
    private String oauthUserId; // oauth 서버측 유저ID
    private String name; // 유저명
    private String email; // 이메일
    @Enumerated(EnumType.STRING)
    private UserRoleType role; // 역할

    public void updateContent(String name, String email) {
        this.name = name;
        this.email = email;
    }

}