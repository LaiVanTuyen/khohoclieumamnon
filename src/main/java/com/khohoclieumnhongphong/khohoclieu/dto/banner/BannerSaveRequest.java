package com.khohoclieumnhongphong.khohoclieu.dto.banner;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BannerSaveRequest {

    @NotBlank(message = "{error.title.notblank}")
    private String title;

    private String linkUrl;

    private boolean isActive = true;

    /**
     * Ảnh (MultipartFile).
     * Sẽ là @NotNull khi tạo (POST), nhưng là optional khi cập nhật (PUT).
     * Chúng ta sẽ xử lý logic này trong Service.
     */
    private MultipartFile image;
}