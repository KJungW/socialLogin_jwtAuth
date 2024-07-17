package com.example.demo.dto.token;

import com.example.demo.type.UserRoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenData {
    Long id;
    UserRoleType roleType;
}
