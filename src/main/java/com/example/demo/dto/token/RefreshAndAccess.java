package com.example.demo.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshAndAccess {
    private String refreshToken;
    private String accessToken;
}
