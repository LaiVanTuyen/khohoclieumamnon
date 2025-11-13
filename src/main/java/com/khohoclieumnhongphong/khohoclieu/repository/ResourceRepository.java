package com.khohoclieumnhongphong.khohoclieu.repository;

import com.khohoclieumnhongphong.khohoclieu.entity.Resource;
import com.khohoclieumnhongphong.khohoclieu.entity.ResourceStatus;
import com.khohoclieumnhongphong.khohoclieu.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Thêm import này

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    // --- CÁC PHƯƠNG THỨC MỚI CHO VIỆC LỌC ---

    // 1. Tìm theo Status (cơ bản)
    Page<Resource> findByStatus(ResourceStatus status, Pageable pageable);

    // 2. Tìm theo Status VÀ Topic
    Page<Resource> findByStatusAndTopic(ResourceStatus status, Topic topic, Pageable pageable);

    // 3. Tìm theo Status VÀ Title (cho thanh tìm kiếm)
    // "ContainingIgnoreCase" nghĩa là tìm kiếm không phân biệt hoa/thường (LIKE %title%)
    Page<Resource> findByStatusAndTitleContainingIgnoreCase(ResourceStatus status, String title, Pageable pageable);

    // 4. Tìm theo Status VÀ Topic VÀ Title
    Page<Resource> findByStatusAndTopicAndTitleContainingIgnoreCase(ResourceStatus status, Topic topic, String title, Pageable pageable);

    // 5. Tìm MỘT tài liệu theo ID và Status (để đảm bảo public chỉ xem được file APPROVED)
    Optional<Resource> findByIdAndStatus(Long id, ResourceStatus status);
}