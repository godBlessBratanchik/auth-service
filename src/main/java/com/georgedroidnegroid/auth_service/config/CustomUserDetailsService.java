package com.georgedroidnegroid.auth_service.config;


import com.georgedroidnegroid.auth_service.entity.User;
import com.georgedroidnegroid.auth_service.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Autowired
    public CustomUserDetailsService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .build();
    }

}