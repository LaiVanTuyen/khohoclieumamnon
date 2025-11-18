package com.khohoclieumnhongphong.khohoclieu.controller;

import com.khohoclieumnhongphong.khohoclieu.dto.banner.BannerResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.banner.BannerSaveRequest;
import com.khohoclieumnhongphong.khohoclieu.service.IBannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
@Tag(name = "5. Banner API", description = "API quản lý Banner trang chủ")
public class BannerController {

    private final IBannerService bannerService;

    // --- API CÔNG KHAI (PUBLIC) ---

    @GetMapping("/active")
    @Operation(summary = "Lấy danh sách banner đang hoạt động (Public)")
    public ResponseEntity<List<BannerResponseDTO>> getActiveBanners() {
        return ResponseEntity.ok(bannerService.getActiveBanners());
    }

    // --- API BẢO MẬT (ADMIN) ---

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy TẤT CẢ banner (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<BannerResponseDTO>> getAllBanners() {
        return ResponseEntity.ok(bannerService.getAllBanners());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy chi tiết 1 banner (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BannerResponseDTO> getBannerById(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.getBannerById(id));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo banner mới (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BannerResponseDTO> createBanner(
            @Valid @ModelAttribute BannerSaveRequest request) {
        // Dùng @ModelAttribute cho multipart/form-data
        BannerResponseDTO response = bannerService.createBanner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật banner (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BannerResponseDTO> updateBanner(
            @PathVariable Long id,
            @Valid @ModelAttribute BannerSaveRequest request) {

        BannerResponseDTO response = bannerService.updateBanner(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa banner (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}