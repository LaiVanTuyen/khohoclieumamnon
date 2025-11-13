package com.khohoclieumnhongphong.khohoclieu.controller; // (Giữ package của bạn)

import com.khohoclieumnhongphong.khohoclieu.dto.resource.CategoryRequestDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.resource.ResourceTypeResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.service.IResourceTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("types") // (Sử dụng /types cho ngắn gọn)
@RequiredArgsConstructor
@Tag(name = "4. Resource Type API", description = "API quản lý Loại tài liệu")
public class ResourceTypeController {

    private final IResourceTypeService resourceTypeService;

    // --- API CÔNG KHAI (PUBLIC) ---

    @GetMapping
    @Operation(summary = "Lấy tất cả loại tài liệu (Public)")
    public ResponseEntity<List<ResourceTypeResponseDTO>> getAllResourceTypes() {
        List<ResourceTypeResponseDTO> types = resourceTypeService.getAllResourceTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết một loại tài liệu (Public)")
    public ResponseEntity<ResourceTypeResponseDTO> getResourceTypeById(@PathVariable Long id) {
        ResourceTypeResponseDTO type = resourceTypeService.getResourceTypeById(id);
        return ResponseEntity.ok(type);
    }

    // --- API BẢO MẬT (ADMIN) ---

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo loại tài liệu mới (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResourceTypeResponseDTO> createResourceType(@Valid @RequestBody CategoryRequestDTO request) {
        ResourceTypeResponseDTO newType = resourceTypeService.createResourceType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newType);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật loại tài liệu (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResourceTypeResponseDTO> updateResourceType(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO request) {
        ResourceTypeResponseDTO updatedType = resourceTypeService.updateResourceType(id, request);
        return ResponseEntity.ok(updatedType);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa loại tài liệu (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteResourceType(@PathVariable Long id) {
        resourceTypeService.deleteResourceType(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}