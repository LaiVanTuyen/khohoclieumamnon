package com.khohoclieumnhongphong.khohoclieu.dto.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResourceUploadRequest {

    @NotBlank(message = "{error.title.notblank}") // <-- Đã sửa
    private String title;

    private String description;

    // Client chỉ cần gửi ID của Topic
    @NotNull(message = "{error.topic.notnull}") // <-- Đã sửa
    private Long topicId;

    // Client chỉ cần gửi ID của Loại
    @NotNull(message = "{error.type.notnull}") // <-- Đã sửa
    private Long typeId;

    // File tải lên (nếu có)
    private MultipartFile file;

    // Hoặc link YouTube (nếu có)
    private String youtubeUrl;
}