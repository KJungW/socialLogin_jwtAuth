package com.example.demo.filter;

import com.example.demo.dto.token.AccessTokenData;
import com.example.demo.service.AuthUserDetailService;
import com.example.demo.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final AuthUserDetailService authUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        try {
            if(checkAuthHeader(authHeader)) {
                String accessToken = authHeader.substring(7);
                AccessTokenData accessTokenData = jwtUtil.decodeAccessToken(accessToken);
                UserDetails userDetails = authUserDetailService.loadUserByUsername(accessTokenData.getId().toString());
                UsernamePasswordAuthenticationToken usernamePasswordToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordToken);
            }
        } catch (Exception e) {
            // AuthenticationFailureHandler에서 처리
        }

        filterChain.doFilter(request, response);
    }

    private Boolean checkAuthHeader(String authHeader) {
        return authHeader != null && authHeader.startsWith("Bearer ");
    }
}