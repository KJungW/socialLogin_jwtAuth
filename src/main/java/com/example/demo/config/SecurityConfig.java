package com.example.demo.config;

import com.example.demo.filter.JWTFilter;
import com.example.demo.handler.AuthenticationFailureHandler;
import com.example.demo.handler.AuthorizationFailureHandler;
import com.example.demo.handler.OAuth2FailureHandler;
import com.example.demo.handler.OAuth2SuccessHandler;
import com.example.demo.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oauth2SuccessHandler;
    private final OAuth2FailureHandler oauth2FailureHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final AuthorizationFailureHandler authorizationFailureHandler;
    private final JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //csrf disable
        http.csrf(AbstractHttpConfigurer::disable);

        //From 로그인 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable);

        //HTTP Basic 인증 방식 disable
        http.httpBasic(AbstractHttpConfigurer::disable);

        //JWTFilter 추가
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        //oauth2 세팅
        //http.oauth2Login(Customizer.withDefaults()); // 디폴트 세팅 적용
        http.oauth2Login((oauth2) -> oauth2
                 .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                        .userService(customOAuth2UserService)) // 커스텀 UserService 등록
                 .failureHandler(oauth2FailureHandler) // 커스텀 failureHandler 등록
                 .successHandler(oauth2SuccessHandler)); // 커스텀 successHandler 등록

        // 인증/인가 예외처리 핸들러 세팅
        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling
                        // 인증은 되었지만 접근 권한이 없을 경우
                        .accessDeniedHandler(authorizationFailureHandler)
                        // 인증에 실패할 경우
                        .authenticationEntryPoint(authenticationFailureHandler)
        );

        //인가규칙 설정 (현재는 모두 허용, 이후 메서드별로 규칙설정)
        http.authorizeHttpRequests(
                authorize -> authorize.anyRequest().permitAll()
        );

        //세션 설정 : STATELESS
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //cors 설정
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource));

        return http.build();
    }
}