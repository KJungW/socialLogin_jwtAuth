package com.example.demo.dto.oauth;

import com.example.demo.type.UserRoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    public Long id;
    public String name;
    public UserRoleType role;
}
