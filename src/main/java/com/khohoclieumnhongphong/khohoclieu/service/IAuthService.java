package com.khohoclieumnhongphong.khohoclieu.service; // (Giữ package của bạn)

import com.khohoclieumnhongphong.khohoclieu.dto.auth.LoginRequest;
import com.khohoclieumnhongphong.khohoclieu.dto.auth.JwtResponse;

public interface IAuthService {

    /**
     * Xử lý nghiệp vụ đăng nhập.
     *
     * @param loginRequest Chứa email và password.
     * @return JwtResponse chứa token và thông tin user.
     */
    JwtResponse login(LoginRequest loginRequest);

    // (Bạn có thể thêm nghiệp vụ đăng ký 'register' ở đây sau)
    // User register(RegisterRequest registerRequest);
}