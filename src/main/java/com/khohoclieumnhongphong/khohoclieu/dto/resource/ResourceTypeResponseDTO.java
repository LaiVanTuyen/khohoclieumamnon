package com.khohoclieumnhongphong.khohoclieu.dto.resource;

import lombok.Data;
import java.time.Instant;

@Data
public class ResourceTypeResponseDTO {
    private Long id;
    private String name;
    private String slug;
    private Instant createdAt;
}