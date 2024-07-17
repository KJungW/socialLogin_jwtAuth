package com.example.demo.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {
    NAVER("naver"),
    GOOGLE("google");
    private final String registrationId;
}
