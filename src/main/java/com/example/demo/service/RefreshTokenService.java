package com.example.demo.service;

import com.example.demo.dto.token.AccessTokenData;
import com.example.demo.dto.token.RefreshTokenData;
import com.example.demo.dto.token.RefreshAndAccess;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.exception.DataNotFound;
import com.example.demo.exception.JwtTokenException;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public Long save(String token) {
        try{
            RefreshTokenData refreshTokenData = jwtUtil.decodeRefreshToken(token);
            User user = userRepository.findById(refreshTokenData.getId()).orElseThrow();
            RefreshToken newTokenRecord = RefreshToken.builder()
                    .user(user)
                    .token(token)
                    .build();
            refreshTokenRepository.findByUser(refreshTokenData.getId()).ifPresentOrElse(
                    tokenRecord -> tokenRecord.rotate(token),
                    () -> refreshTokenRepository.save(newTokenRecord)
            );
            return newTokenRecord.getId();
        } catch (Exception e) {
            throw new JwtTokenException("refresh 토큰이 유효하지 않습니다.");
        }
    }

    @Transactional
    public void delete(String inputRefreshToken) {
        try {
            RefreshTokenData refreshTokenData = jwtUtil.decodeRefreshToken(inputRefreshToken);
            RefreshToken tokenRecord = refreshTokenRepository.findByUser(refreshTokenData.getId()).get();
            compareRefreshTokenWithRecord(inputRefreshToken, tokenRecord);
            refreshTokenRepository.delete(tokenRecord);
        } catch (Exception ex) {
            throw new JwtTokenException("refresh 토큰이 유효하지 않습니다.");
        }
    }

    @Transactional
    public RefreshAndAccess reissueAccessToken(String inputRefreshToken) {
        try {
            RefreshTokenData refreshTokenData = jwtUtil.decodeRefreshToken(inputRefreshToken);
            RefreshToken tokenRecord = refreshTokenRepository.findByUser(refreshTokenData.getId()).get();
            compareRefreshTokenWithRecord(inputRefreshToken, tokenRecord);
            RefreshAndAccess reissueResult = makeReissueResult(refreshTokenData);
            tokenRecord.rotate(reissueResult.getRefreshToken());
            return reissueResult;
        } catch (Exception ex) {
            throw new JwtTokenException("refresh 토큰이 유효하지 않습니다.");
        }
    }

    private void compareRefreshTokenWithRecord(String inputRefreshToken, RefreshToken tokenRecord ) {
        if(!inputRefreshToken.equals(tokenRecord.getToken())) {
            throw new JwtTokenException("refresh 토큰이 유효하지 않습니다.");
        }
    }

    private RefreshAndAccess makeReissueResult(RefreshTokenData refreshTokenData) {
        AccessTokenData accessTokenData = new AccessTokenData(refreshTokenData.getId(), refreshTokenData.getRoleType());
        String newAccessToken = jwtUtil.createAccessToken(accessTokenData);
        String newRefreshToken = jwtUtil.createRefreshToken(refreshTokenData);
        return new RefreshAndAccess(newRefreshToken, newAccessToken);
    }

}
