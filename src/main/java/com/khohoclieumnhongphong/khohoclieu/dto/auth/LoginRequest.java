package com.khohoclieumnhongphong.khohoclieu.dto.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "{error.email.notblank}") // <-- Đã sửa
    @Email(message = "{error.email.invalid}")    // <-- Đã sửa
    private String email;

    @NotBlank(message = "{error.password.notblank}") // <-- Đã sửa
    private String password;
}