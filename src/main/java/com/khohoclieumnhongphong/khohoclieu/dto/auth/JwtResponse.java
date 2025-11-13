package com.khohoclieumnhongphong.khohoclieu.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type;
    private Long id;
    private String email;
    private String fullName;
    private List<String> roles;
}