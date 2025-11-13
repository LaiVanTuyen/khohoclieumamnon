package com.khohoclieumnhongphong.khohoclieu.controller; // (Giữ package của bạn)

import com.khohoclieumnhongphong.khohoclieu.dto.user.AdminUserResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.user.UserCreateRequest;
import com.khohoclieumnhongphong.khohoclieu.dto.user.UserUpdateRequest;
import com.khohoclieumnhongphong.khohoclieu.service.IUserService;
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
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name = "6. User Management API", description = "API quản lý người dùng (Admin Only)")
@PreAuthorize("hasRole('ADMIN')") // Bảo mật TOÀN BỘ controller này cho Admin
@SecurityRequirement(name = "bearerAuth") // Yêu cầu token cho tất cả API
public class UserController {

    private final IUserService userService;

    @GetMapping
    @Operation(summary = "Lấy tất cả người dùng (Admin)")
    public ResponseEntity<List<AdminUserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết 1 người dùng (Admin)")
    public ResponseEntity<AdminUserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @Operation(summary = "Tạo người dùng mới (Admin)")
    public ResponseEntity<AdminUserResponseDTO> createUser(@Valid @RequestBody UserCreateRequest request) {
        AdminUserResponseDTO newUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật người dùng (Admin)")
    public ResponseEntity<AdminUserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        AdminUserResponseDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa người dùng (Admin)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}