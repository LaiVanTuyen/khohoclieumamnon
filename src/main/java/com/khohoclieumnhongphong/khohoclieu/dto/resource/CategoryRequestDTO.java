package com.khohoclieumnhongphong.khohoclieu.dto.resource;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Dùng chung cho việc tạo/cập nhật Topic và ResourceType
@Data
public class CategoryRequestDTO {

    @NotBlank(message = "{error.name.notblank}") // <-- Đã sửa
    private String name;

    private String description; // (Optional)
}