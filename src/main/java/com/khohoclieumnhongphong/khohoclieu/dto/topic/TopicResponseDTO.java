package com.khohoclieumnhongphong.khohoclieu.dto.topic;

import lombok.Data;

import java.time.Instant;

@Data
public class TopicResponseDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Instant createdAt;
}
