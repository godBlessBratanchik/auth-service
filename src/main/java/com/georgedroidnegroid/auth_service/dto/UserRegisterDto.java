package com.georgedroidnegroid.auth_service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRegisterDto {
    private String email;
    private String password;
}
