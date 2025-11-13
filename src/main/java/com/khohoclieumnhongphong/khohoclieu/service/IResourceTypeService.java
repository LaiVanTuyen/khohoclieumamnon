package com.khohoclieumnhongphong.khohoclieu.service; // (Giữ package của bạn)

import com.khohoclieumnhongphong.khohoclieu.dto.resource.CategoryRequestDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.resource.ResourceTypeResponseDTO;

import java.util.List;

public interface IResourceTypeService {

    // Lấy tất cả loại tài liệu (cho public)
    List<ResourceTypeResponseDTO> getAllResourceTypes();

    // Lấy một loại theo ID (cho public)
    ResourceTypeResponseDTO getResourceTypeById(Long id);

    // Tạo loại mới (cho admin)
    ResourceTypeResponseDTO createResourceType(CategoryRequestDTO request);

    // Cập nhật loại (cho admin)
    ResourceTypeResponseDTO updateResourceType(Long id, CategoryRequestDTO request);

    // Xóa loại (cho admin)
    void deleteResourceType(Long id);
}