package com.example.demo.service;

import com.example.demo.dto.oauth.OAuth2UserPrinciple;
import com.example.demo.dto.oauth.UserDto;
import com.example.demo.dto.oauth.user_info.GoogleUserInfo;
import com.example.demo.dto.oauth.user_info.NaverUserInfo;
import com.example.demo.dto.oauth.user_info.OAuth2UserInfo;
import com.example.demo.entity.User;
import com.example.demo.type.LoginType;
import com.example.demo.type.UserRoleType;
import com.example.demo.exception.OAuth2AuthenticationProcessingException;
import com.example.demo.repository.UserRepository;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional()
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            OAuth2UserInfo oAuth2UserInfo = makeOAuthUserInfo(userRequest, oAuth2User);
            Optional<User> user = findUserByResponse(oAuth2UserInfo);
            return user.map(userEntity -> updateUser(userEntity, oAuth2UserInfo))
                    .orElseGet(() -> createNewUser(oAuth2UserInfo));
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }

        // AuthenticationException을 상속받는 예외발생은
        // 이후 OAuth2AuthenticationFailureHandler를 실행시키는 트리커가 된다.
    }

    private OAuth2UserInfo makeOAuthUserInfo(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2Response = null;
        if (registrationId.equals(LoginType.NAVER.getRegistrationId())) {
            oAuth2Response = new NaverUserInfo(oAuth2User.getAttributes());
            return oAuth2Response;
        } else if (registrationId.equals(LoginType.GOOGLE.getRegistrationId())) {
            oAuth2Response = new GoogleUserInfo(oAuth2User.getAttributes());
            return oAuth2Response;
        } else {
            throw new OAuth2AuthenticationProcessingException("지원하지 않는 oauth2 서비스입니다.");
        }
    }

    private Optional<User> findUserByResponse(OAuth2UserInfo oAuth2UserInfo) {
        return userRepository.findByLoginTypeAndOauthUserId(
                LoginType.valueOf(oAuth2UserInfo.getProvider()),
                oAuth2UserInfo.getProviderId()
        );
    }

    private OAuth2UserPrinciple createNewUser(OAuth2UserInfo oAuth2UserInfo) {

        User user = User.builder()
                .loginType(LoginType.valueOf(oAuth2UserInfo.getProvider()))
                .oauthUserId(oAuth2UserInfo.getProviderId())
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .role(UserRoleType.ROLE_USER)
                .build();
        userRepository.save(user);

        return new OAuth2UserPrinciple(new UserDto(user.getId(), user.getName(), user.getRole()));
    }

    private OAuth2UserPrinciple updateUser(User user, OAuth2UserInfo oAuth2UserInfo) {
        user.updateContent(oAuth2UserInfo.getName(), oAuth2UserInfo.getEmail());
        userRepository.save(user);

        return new OAuth2UserPrinciple(new UserDto(user.getId(), user.getName(), user.getRole()));
    }
}