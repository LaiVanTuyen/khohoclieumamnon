package com.khohoclieumnhongphong.khohoclieu.dto.resource;


import com.khohoclieumnhongphong.khohoclieu.dto.topic.TopicResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.user.UserResponseDTO;
import lombok.Data;
import java.time.Instant;

@Data
public class ResourceResponseDTO {

    // --- Thông tin cơ bản ---
    private Long id;
    private String title;
    private String description;

    // --- Thông tin File (Đã xử lý) ---

    /**
     * Đây là Full URL để truy cập file (ví dụ: http://minio.com/bucket/file.mp4).
     * Trường này sẽ được Service tính toán bằng cách kết hợp
     * 'filePath' (từ Entity) với MinIO base URL (từ config).
     */
    private String fileUrl;

    private String originalFileName;
    private Long fileSize; // Kích thước file (bytes)

    // --- Thông tin Link ngoài ---
    private String youtubeUrl; // Link video YouTube (nếu có)

    // --- Thông tin Metadata (Quan hệ) ---
    // Lồng các DTO Response khác vào để client không cần gọi thêm API

    private UserResponseDTO uploader;     // Người tải lên
    private UserResponseDTO approver;     // Người duyệt (có thể null)
    private TopicResponseDTO topic;       // Thuộc chủ đề nào
    private ResourceTypeResponseDTO type; // Thuộc loại nào
    private ResourceStatusResponseDTO status; // Trạng thái (PENDING, APPROVED...)

    // --- Thông tin thời gian ---
    private Instant createdAt;    // Ngày tải lên
    private Instant approvedAt; // Ngày được duyệt (có thể null)
}