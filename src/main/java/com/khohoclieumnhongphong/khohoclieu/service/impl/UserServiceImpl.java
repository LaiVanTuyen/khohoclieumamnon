package com.khohoclieumnhongphong.khohoclieu.service.impl; // (Giữ package của bạn)

import com.khohoclieumnhongphong.khohoclieu.dto.user.AdminUserResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.user.UserCreateRequest;
import com.khohoclieumnhongphong.khohoclieu.dto.user.UserUpdateRequest;
import com.khohoclieumnhongphong.khohoclieu.entity.ERole; // Enum bạn đã tạo
import com.khohoclieumnhongphong.khohoclieu.entity.Role;
import com.khohoclieumnhongphong.khohoclieu.entity.User;
import com.khohoclieumnhongphong.khohoclieu.exception.ResourceNotFoundException;
import com.khohoclieumnhongphong.khohoclieu.repository.RoleRepository;
import com.khohoclieumnhongphong.khohoclieu.repository.UserRepository;
import com.khohoclieumnhongphong.khohoclieu.service.IUserService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // Inject Bean từ SecurityConfig

    // Helper (private) map Entity sang DTO
    private AdminUserResponseDTO mapToDTO(User user) {
        AdminUserResponseDTO dto = new AdminUserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setCreatedAt(user.getCreatedAt());
        // Chuyển Set<Role> sang List<String>
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()));
        return dto;
    }

    // Helper (private) tìm Role Entities từ Set<String>
    private Set<Role> findRolesByNames(Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            ERole eRole = ERole.valueOf(roleName); // Chuyển String sang Enum
            Role role = roleRepository.findByName(eRole)
                    .orElseThrow(() -> new ResourceNotFoundException("error.role.notfound")); // i18n
            roles.add(role);
        }
        return roles;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.user.notfound"));
        return mapToDTO(user);
    }

    @Override
    @Transactional
    public AdminUserResponseDTO createUser(UserCreateRequest request) {
        // 1. Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("error.email.exists"); // i18n
        }

        // 2. Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. Tìm các Role Entity
        Set<Role> roles = findRolesByNames(request.getRoles());

        // 4. Tạo User Entity
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setFullName(request.getFullName());
        newUser.setPassword(encodedPassword);
        newUser.setRoles(roles);

        // 5. Lưu và trả về
        User savedUser = userRepository.save(newUser);
        return mapToDTO(savedUser);
    }

    @Override
    @Transactional
    public AdminUserResponseDTO updateUser(Long id, UserUpdateRequest request) {
        // 1. Tìm user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.user.notfound"));

        // 2. Tìm các Role Entity mới
        Set<Role> roles = findRolesByNames(request.getRoles());

        // 3. Cập nhật
        user.setFullName(request.getFullName());
        user.setRoles(roles);
        // (Admin không được phép đổi email hoặc mật khẩu ở đây)

        // 4. Lưu và trả về
        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.user.notfound"));

        // Lưu ý: Nếu user này đã upload tài liệu,
        // database (ON DELETE RESTRICT) sẽ CHẶN việc xóa này.
        // Đây là hành vi ĐÚNG để bảo vệ dữ liệu.
        // Bạn nên bắt lỗi DataIntegrityViolationException giống như Topic/Type

        userRepository.delete(user);
    }
}