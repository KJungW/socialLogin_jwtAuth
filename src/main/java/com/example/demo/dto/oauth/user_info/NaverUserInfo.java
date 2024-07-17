package com.example.demo.dto.oauth.user_info;

import com.example.demo.type.LoginType;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attribute;

    public NaverUserInfo(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {
        return LoginType.NAVER.toString();
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}