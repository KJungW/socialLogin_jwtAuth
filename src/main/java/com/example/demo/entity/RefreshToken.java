package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Builder
    public RefreshToken(Long id, User user, String token) {
        this.id = id;
        this.user = user;
        this.token = token;
    }

    @Id @GeneratedValue
    private Long id;
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;
    private String token;

    public void rotate(String token) {
        this.token = token;
    }
}
