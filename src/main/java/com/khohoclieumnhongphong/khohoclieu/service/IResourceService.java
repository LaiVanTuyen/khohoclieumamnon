package com.khohoclieumnhongphong.khohoclieu.service;

import com.khohoclieumnhongphong.khohoclieu.dto.resource.ResourceResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.resource.ResourceUpdateRequest;
import com.khohoclieumnhongphong.khohoclieu.dto.resource.ResourceUploadRequest;
import com.khohoclieumnhongphong.khohoclieu.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IResourceService {

    /**
     * Nghiệp vụ chính: Tạo một tài liệu mới (upload file hoặc link YouTube).
     *
     * @param request DTO chứa thông tin (title, topicId, typeId) và file.
     * @param uploader User entity của người đang đăng nhập (lấy từ Security).
     * @return DTO Response của tài liệu vừa được tạo.
     */
    ResourceResponseDTO createResource(ResourceUploadRequest request, User uploader);

    /**
     * Lấy danh sách tài liệu CÔNG KHAI (đã APPROVED).
     * Hỗ trợ phân trang, tìm kiếm theo tiêu đề, và lọc theo chủ đề.
     *
     * @param topicId     ID của chủ đề (nếu = null, bỏ qua lọc)
     * @param searchQuery Từ khóa tìm kiếm (nếu = null, bỏ qua lọc)
     * @param pageable    Thông tin phân trang (page, size)
     * @return Một trang (Page) chứa các DTO tài liệu
     */
    Page<ResourceResponseDTO> getPublicResources(Long topicId, String searchQuery, Pageable pageable);

    /**
     * Lấy chi tiết một tài liệu CÔNG KHAI (đã APPROVED).
     *
     * @param id ID của tài liệu
     * @return DTO Response của tài liệu
     */
    ResourceResponseDTO getPublicResourceById(Long id);

    /**
     * Phê duyệt một tài liệu (Chuyển từ PENDING sang APPROVED).
     * Chỉ dành cho Admin.
     *
     * @param id ID của tài liệu cần duyệt
     * @param approver User entity của Admin đang duyệt
     * @return DTO của tài liệu đã được duyệt
     */
    ResourceResponseDTO approveResource(Long id, User approver);

    /**
     * Cập nhật thông tin (metadata) của một tài liệu.
     * Chỉ dành cho Admin hoặc người đã upload.
     *
     * @param id ID của tài liệu cần cập nhật
     * @param request DTO chứa thông tin mới (title, description...)
     * @return DTO của tài liệu đã được cập nhật
     */
    ResourceResponseDTO updateResource(Long id, ResourceUpdateRequest request); // Cần tạo DTO này

    /**
     * Xóa một tài liệu (Xóa file trên MinIO và xóa metadata trong DB).
     * Chỉ dành cho Admin hoặc người đã upload.
     *
     * @param id ID của tài liệu cần xóa
     */
    void deleteResource(Long id);
}