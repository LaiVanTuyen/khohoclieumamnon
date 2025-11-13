package com.khohoclieumnhongphong.khohoclieu.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "{error.name.notblank}")
    private String fullName;

    @NotNull(message = "{error.roles.notnull}")
    private Set<String> roles;

    // (Chúng ta có thể thêm 1 trường boolean 'isActive' ở đây nếu muốn)
}