package com.khohoclieumnhongphong.khohoclieu.service.impl; // (Giữ package của bạn)

import com.khohoclieumnhongphong.khohoclieu.dto.auth.LoginRequest;
import com.khohoclieumnhongphong.khohoclieu.dto.auth.JwtResponse;
import com.khohoclieumnhongphong.khohoclieu.entity.Role;
import com.khohoclieumnhongphong.khohoclieu.entity.User;
import com.khohoclieumnhongphong.khohoclieu.exception.ResourceNotFoundException;
import com.khohoclieumnhongphong.khohoclieu.repository.UserRepository;
import com.khohoclieumnhongphong.khohoclieu.security.jwt.JwtTokenProvider;
import com.khohoclieumnhongphong.khohoclieu.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Tự động inject các dependency final
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    @Override
    public JwtResponse login(LoginRequest loginRequest) {

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        // 2. [QUAN TRỌNG] Yêu cầu Spring Security xác thực
        // Nó sẽ tự động gọi UserDetailsServiceImpl và PasswordEncoder
        Authentication authentication = authenticationManager.authenticate(authToken);

        // 3. Nếu xác thực thành công, set Authentication vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. Tạo JWT Token
        String jwt = jwtTokenProvider.generateToken(authentication);

        // 5. Lấy thông tin User đầy đủ để trả về (an toàn, vì đã xác thực)
        // Lấy UserDetails từ đối tượng Authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Lấy entity User từ DB để có thêm thông tin (fullName, id)
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound")); // Key i18n

        // 6. Chuyển đổi Set<Role> thành List<String> (ví dụ: ["ROLE_ADMIN", "ROLE_TEACHER"])
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        // 7. Trả về JwtResponse DTO
        // Sửa dòng này:
        return new JwtResponse(
                jwt,
                "Bearer", // <-- Thêm tham số "type" còn thiếu
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                roles
        );
    }
}