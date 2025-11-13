package com.khohoclieumnhongphong.khohoclieu.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserCreateRequest {

    @NotBlank(message = "{error.email.notblank}")
    @Email(message = "{error.email.invalid}")
    private String email;

    @NotBlank(message = "{error.password.notblank}")
    @Size(min = 6, message = "{error.password.size}") // Mật khẩu ít nhất 6 ký tự
    private String password;

    @NotBlank(message = "{error.name.notblank}")
    private String fullName;

    @NotNull(message = "{error.roles.notnull}")
    private Set<String> roles; // (ví dụ: ["ROLE_ADMIN", "ROLE_TEACHER"])
}