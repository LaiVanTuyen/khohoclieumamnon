package com.khohoclieumnhongphong.khohoclieu.dto.resource;

import com.khohoclieumnhongphong.khohoclieu.entity.EResourceStatus;
import lombok.Data;

@Data
public class ResourceStatusResponseDTO {
    private Long id;

    // Tên trạng thái: PENDING, APPROVED, REJECTED
    private EResourceStatus name;
}
