package com.khohoclieumnhongphong.khohoclieu.dto.user;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class AdminUserResponseDTO {
    private Long id;
    private String email;
    private String fullName;
    private Instant createdAt;
    private List<String> roles;
}