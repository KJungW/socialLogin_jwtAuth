package com.example.demo.controller.auth;

import com.example.demo.controller.auth.output.ReissueOutput;
import com.example.demo.dto.token.RefreshAndAccess;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.util.CookieUtil;
import com.example.demo.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/reissue")
    public ReissueOutput reissue(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = cookieUtil.findCookie("refresh", cookies);
        RefreshAndAccess reissueResult = refreshTokenService.reissueAccessToken(refreshToken);
        Cookie cookie = cookieUtil.createCookie("refresh", reissueResult.getRefreshToken(), (int) (jwtUtil.getRefreshExpirationTimeMs() / 1000));
        response.addCookie(cookie);
        return new ReissueOutput(reissueResult.getAccessToken());
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = cookieUtil.findCookie("refresh", cookies);
        if(!refreshToken.isEmpty()) {
            refreshTokenService.delete(refreshToken);
            Cookie cookie = cookieUtil.createCookie("refresh", "", 0);
            response.addCookie(cookie);
        }
    }
}
