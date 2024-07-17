package com.example.demo.service;

import com.example.demo.dto.auth.AuthUserDetail;
import com.example.demo.entity.User;
import com.example.demo.exception.DataNotFound;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long id = Long.valueOf(username);
        User user = userRepository.findById(id).orElseThrow(
                () ->new DataNotFound("현재 ID에 해당하는 사용자가 존재하지 않습니다.")
        );

        return new AuthUserDetail(user.getId(), user.getRole());
    }
}
