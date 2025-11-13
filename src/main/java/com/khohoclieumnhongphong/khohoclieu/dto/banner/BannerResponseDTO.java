package com.khohoclieumnhongphong.khohoclieu.dto.banner;

import lombok.Data;
import java.time.Instant;

@Data
public class BannerResponseDTO {
    private Long id;
    private String title;
    private String imageUrl; // Full URL từ MinIO
    private String linkUrl;
    private boolean isActive;
    private Instant createdAt;
}