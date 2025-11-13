package com.georgedroidnegroid.auth_service.service;

import com.georgedroidnegroid.auth_service.entity.User;
import com.georgedroidnegroid.auth_service.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void save(User user) {
        authRepository.save(user);
    }

    @Transactional
    public void delete(User user) {
        authRepository.delete(user);
    }

    @Transactional
    public void registerUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        save(user);
    }

    @Transactional
    public User login(String email, String password) {
        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid password");
        }

        return user;
    }

    public Optional<User> findByEmail(String email) {
        return authRepository.findByEmail(email);
    }

    public Optional<User> findById(Integer organizerId) {
        return authRepository.findById(organizerId);
    }
}
