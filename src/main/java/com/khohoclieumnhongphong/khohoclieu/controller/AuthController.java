package com.khohoclieumnhongphong.khohoclieu.controller;

import com.khohoclieumnhongphong.khohoclieu.dto.auth.LoginRequest;
import com.khohoclieumnhongphong.khohoclieu.dto.auth.JwtResponse;
import com.khohoclieumnhongphong.khohoclieu.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication API", description = "API cho việc Đăng nhập") // Tag cho Swagger
public class AuthController {

    private final IAuthService authService;

    /**
     * Xử lý yêu cầu đăng nhập từ client.
     * @param loginRequest DTO chứa email và password (đã được @Valid)
     * @return ResponseEntity chứa JwtResponse (token và thông tin user)
     */
    @Operation(summary = "Đăng nhập hệ thống",
            description = "Endpoint này xác thực email và mật khẩu, trả về một JWT token nếu thành công.")
    @ApiResponse(responseCode = "200", description = "Đăng nhập thành công")
    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ (ví dụ: email trống)")
    @ApiResponse(responseCode = "401", description = "Xác thực thất bại (sai email hoặc mật khẩu)")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        // 1. Dùng @Valid để kích hoạt validation (i18n keys) trong LoginRequest.
        // Nếu thất bại, GlobalExceptionHandler sẽ tự động bắt và trả về lỗi 400.

        // 2. Nếu validation thành công, gọi AuthService để xử lý nghiệp vụ
        JwtResponse jwtResponse = authService.login(loginRequest);

        // 3. Trả về token và thông tin user cho client
        return ResponseEntity.ok(jwtResponse);
    }

}
