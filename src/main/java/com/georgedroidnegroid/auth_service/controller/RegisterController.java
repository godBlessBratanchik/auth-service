package com.georgedroidnegroid.auth_service.controller;

import com.georgedroidnegroid.auth_service.api.RegisterApi;
import com.georgedroidnegroid.auth_service.config.JwtUtil;
import com.georgedroidnegroid.auth_service.entity.User;
import com.georgedroidnegroid.auth_service.utils.exeption.UserNotCreatedException;
import com.georgedroidnegroid.auth_service.utils.exeption.UserNotFoundException;
import com.georgedroidnegroid.auth_service.dto.UserRegisterDto;
import com.georgedroidnegroid.auth_service.service.AuthService;
import com.georgedroidnegroid.auth_service.utils.errorresponse.UserErrorResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

import static com.georgedroidnegroid.auth_service.api.RegisterApi.REQUEST_URL;

@RestController
@RequestMapping(REQUEST_URL)
public class RegisterController implements RegisterApi {
    private final JwtUtil jwtUtil;
    private final AuthService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public RegisterController(JwtUtil jwtUtil, AuthService userService, ModelMapper modelMapper) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<?> performLogin(UserRegisterDto loginUserDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Validation failed"));
        }

        try {
            User user = userService.login(loginUserDto.getEmail(), loginUserDto.getPassword());
            String token = jwtUtil.generateToken(user.getEmail(), user.getId());
            return ResponseEntity.ok(Map.of("jwtToken", token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> performRegister(UserRegisterDto userDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Validation failed"));
        }

        User user = convertToUser(userDto);
        userService.registerUser( user.getEmail(), user.getPasswordHash());

        Optional<User> savedUserOptional = userService.findByEmail(user.getEmail());

        User savedUser = savedUserOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found after registration"));

        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId());


        return ResponseEntity.ok(Map.of("jwtToken", token));
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        UserErrorResponse response = new UserErrorResponse(
                "User with this id wasn't found", System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(), System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    public User convertToUser(UserRegisterDto userDto) {
        return this.modelMapper.map(userDto, User.class);
    }

}
