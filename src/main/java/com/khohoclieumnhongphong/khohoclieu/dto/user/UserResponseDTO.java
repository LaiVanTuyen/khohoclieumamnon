package com.khohoclieumnhongphong.khohoclieu.dto.user;

import lombok.Data;
import java.time.Instant;

// Dùng khi cần lồng thông tin User vào các DTO khác (ví dụ: ResourceResponse)
@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String fullName;
    private Instant createdAt;
}
