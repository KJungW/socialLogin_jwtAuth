package com.example.demo.handler;


import com.example.demo.dto.exception.ErrorResult;
import com.example.demo.type.ErrorCode;
import com.example.demo.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthorizationFailureHandler implements AccessDeniedHandler {
    private final JsonUtil jsonUtil;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        String content = jsonUtil.convertObjectToJson(
                new ErrorResult(ErrorCode.FORBIDDEN,"권한이 없습니다."));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(content);
    }
}