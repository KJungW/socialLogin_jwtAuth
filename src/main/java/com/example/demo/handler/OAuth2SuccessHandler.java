package com.example.demo.handler;

import com.example.demo.dto.oauth.OAuth2UserPrinciple;
import com.example.demo.dto.token.RefreshTokenData;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.type.UserRoleType;
import com.example.demo.util.CookieUtil;
import com.example.demo.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2UserPrinciple userPrinciple = (OAuth2UserPrinciple) authentication.getPrincipal();

        // refresh 토큰에 담을 데이터 수집
        Long id = userPrinciple.getId();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        UserRoleType role = UserRoleType.valueOf(auth.getAuthority());

        // refresh 토큰을 생성하고 DB에 저장
        String refreshToken = jwtUtil.createRefreshToken(new RefreshTokenData(id, role));
        refreshTokenService.save(refreshToken);

        // refresh 토큰을 쿠키에 담는다.
        Cookie cookie = cookieUtil.createCookie("refresh", refreshToken, (int) (jwtUtil.getRefreshExpirationTimeMs() / 1000));
        response.addCookie(cookie);
        
        // 로그인/회원가입 완료페이지로 리다이렉션
        response.sendRedirect("http://localhost:3000/exit");
    }

}