package com.georgedroidnegroid.auth_service.api;


import com.georgedroidnegroid.auth_service.dto.UserRegisterDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Optional;

public interface RegisterApi {
    final String REQUEST_URL = "auth";

    @PostMapping("/login")
    ResponseEntity<?> performLogin(
            @RequestBody @Valid UserRegisterDto loginUserDto,
            BindingResult bindingResult);


    @PostMapping("/register")
    ResponseEntity<?> performRegister(
            @RequestBody @Valid UserRegisterDto userDto,
            BindingResult bindingResult);











}
