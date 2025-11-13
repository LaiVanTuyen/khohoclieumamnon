package com.khohoclieumnhongphong.khohoclieu.security;

import com.khohoclieumnhongphong.khohoclieu.entity.Resource;
import com.khohoclieumnhongphong.khohoclieu.entity.User;
import com.khohoclieumnhongphong.khohoclieu.exception.ResourceNotFoundException;
import com.khohoclieumnhongphong.khohoclieu.repository.ResourceRepository;
import com.khohoclieumnhongphong.khohoclieu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("resourceSecurity")
@RequiredArgsConstructor
public class ResourceSecurity {

    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    /**
     * Kiểm tra xem user đang đăng nhập có phải là người đã upload resource này không.
     *
     * @param authentication Đối tượng xác thực (từ Spring)
     * @param resourceId     ID của tài liệu cần kiểm tra
     * @return true nếu là chủ sở hữu, false nếu không
     */
    public boolean isUploader(Authentication authentication, Long resourceId) {
        // 1. Lấy email của user đang đăng nhập
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();

        // 2. Lấy User entity
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("error.user.notfound"));

        // 3. Lấy Resource entity
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("error.resource.notfound"));

        // 4. So sánh ID của người upload với ID của user đang đăng nhập
        return resource.getUploader().getId().equals(currentUser.getId());
    }
}