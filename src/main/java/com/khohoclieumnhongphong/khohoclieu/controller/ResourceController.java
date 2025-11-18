package com.khohoclieumnhongphong.khohoclieu.controller; // (Giữ package của bạn)

import com.khohoclieumnhongphong.khohoclieu.dto.resource.ResourceResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.resource.ResourceUpdateRequest;
import com.khohoclieumnhongphong.khohoclieu.dto.resource.ResourceUploadRequest;
import com.khohoclieumnhongphong.khohoclieu.entity.User;
import com.khohoclieumnhongphong.khohoclieu.exception.ResourceNotFoundException;
import com.khohoclieumnhongphong.khohoclieu.repository.UserRepository;
import com.khohoclieumnhongphong.khohoclieu.service.IResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("resources")
@RequiredArgsConstructor
@Tag(name = "2. Resource API", description = "API quản lý Tài liệu học tập")
public class ResourceController {

    private final IResourceService resourceService;
    private final UserRepository userRepository; // Cần repo này để lấy Entity User

    /**
     * Endpoint để upload tài liệu mới (file hoặc link).
     * Yêu cầu quyền ADMIN hoặc TEACHER (đã cấu hình trong SecurityConfig).
     *
     * @param request DTO chứa (title, file, youtubeUrl...)
     * @param authentication Được Spring Security tự động inject, chứa thông tin user
     * @return DTO của tài liệu vừa tạo
     */

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}) // Báo cho Swagger/Spring biết đây là multipart
    @Operation(summary = "Tải lên tài liệu mới",
            description = "Tải lên file hoặc gửi link YouTube. Yêu cầu quyền ADMIN hoặc TEACHER.",
            security = @SecurityRequirement(name = "bearerAuth")) // Báo Swagger endpoint này cần token
    public ResponseEntity<ResourceResponseDTO> uploadResource(
            @Valid @ModelAttribute ResourceUploadRequest request, // Dùng @ModelAttribute cho multipart/form-data
            Authentication authentication) {

        // --- LẤY USER ĐANG ĐĂNG NHẬP ---
        // 1. Lấy email từ principal (token)
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();

        // 2. Lấy User Entity từ email
        User uploader = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("error.user.notfound"));

        // Gọi service để xử lý nghiệp vụ
        ResourceResponseDTO responseDTO = resourceService.createResource(request, uploader);

        // Trả về 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tài liệu (Public, Phân trang)")
    public ResponseEntity<Page<ResourceResponseDTO>> getPublicResources(
            // @RequestParam(required = false) cho phép param này có thể null
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String search,

            // @PageableDefault cho phép thiết lập phân trang mặc định
            // Ví dụ: ?page=0&size=10&sort=createdAt,desc
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable)
    {
        Page<ResourceResponseDTO> resourcePage = resourceService.getPublicResources(topicId, search, pageable);
        return ResponseEntity.ok(resourcePage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết tài liệu (Public)")
    public ResponseEntity<ResourceResponseDTO> getPublicResourceById(@PathVariable Long id) {
        ResourceResponseDTO resource = resourceService.getPublicResourceById(id);
        return ResponseEntity.ok(resource);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN
    @Operation(summary = "Phê duyệt tài liệu (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResourceResponseDTO> approveResource(
            @PathVariable Long id,
            Authentication authentication) {

        // Lấy admin user đang duyệt
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        User approver = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("error.user.notfound"));

        ResourceResponseDTO responseDTO = resourceService.approveResource(id, approver);
        return ResponseEntity.ok(responseDTO);
    }


    @PutMapping("/{id}")
    // Dùng @resourceSecurity (Bean đã tạo) để kiểm tra quyền
    @PreAuthorize("hasRole('ADMIN') or @resourceSecurity.isUploader(authentication, #id)")
    @Operation(summary = "Cập nhật thông tin tài liệu (Admin hoặc Chủ sở hữu)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResourceResponseDTO> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceUpdateRequest request) {

        ResourceResponseDTO responseDTO = resourceService.updateResource(id, request);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    // Dùng @resourceSecurity (Bean đã tạo) để kiểm tra quyền
    @PreAuthorize("hasRole('ADMIN') or @resourceSecurity.isUploader(authentication, #id)")
    @Operation(summary = "Xóa tài liệu (Admin hoặc Chủ sở hữu)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
    // (Các endpoint GET, PUT, DELETE... sẽ được thêm ở đây)
}