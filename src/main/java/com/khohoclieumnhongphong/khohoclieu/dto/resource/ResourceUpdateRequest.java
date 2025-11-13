package com.khohoclieumnhongphong.khohoclieu.dto.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceUpdateRequest {

    @NotBlank(message = "{error.title.notblank}")
    private String title;

    private String description;

    @NotNull(message = "{error.topic.notnull}")
    private Long topicId;

    @NotNull(message = "{error.type.notnull}")
    private Long typeId;

    // (Chúng ta bỏ qua việc cập nhật file/link YouTube để giữ logic đơn giản)
}