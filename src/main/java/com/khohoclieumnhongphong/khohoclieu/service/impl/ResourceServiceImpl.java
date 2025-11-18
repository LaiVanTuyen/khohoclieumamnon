package com.khohoclieumnhongphong.khohoclieu.service.impl; // (Giữ package của bạn)

import com.khohoclieumnhongphong.khohoclieu.dto.resource.*;
import com.khohoclieumnhongphong.khohoclieu.dto.topic.TopicResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.user.UserResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.entity.*;
import com.khohoclieumnhongphong.khohoclieu.exception.FileUploadException;
import com.khohoclieumnhongphong.khohoclieu.exception.ResourceNotFoundException;
import com.khohoclieumnhongphong.khohoclieu.repository.*;
import com.khohoclieumnhongphong.khohoclieu.service.IMinioService;
import com.khohoclieumnhongphong.khohoclieu.service.IResourceService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.lang3.StringUtils;
import com.khohoclieumnhongphong.khohoclieu.dto.resource.ResourceUpdateRequest;
import com.khohoclieumnhongphong.khohoclieu.entity.User;
import java.time.Instant;

@Service
@RequiredArgsConstructor // Tự động inject tất cả repository và service
public class ResourceServiceImpl implements IResourceService {

    // Inject các Repository cần thiết
    private final ResourceRepository resourceRepository;
    private final TopicRepository topicRepository;
    private final ResourceTypeRepository resourceTypeRepository;
    private final ResourceStatusRepository resourceStatusRepository;

    // Inject service MinIO
    private final IMinioService minioService;

    // Trạng thái mặc định khi upload
    private static final EResourceStatus DEFAULT_STATUS = EResourceStatus.PENDING;
    // Thư mục lưu trên MinIO
    private static final String MINIO_FOLDER = "resources";
    // Trạng thái công khai
    private static final EResourceStatus PUBLIC_STATUS = EResourceStatus.APPROVED;
    // Trạng thái duyệt
    private static final EResourceStatus APPROVED_STATUS = EResourceStatus.APPROVED;

    @Override
    @Transactional // Đảm bảo tất cả thành công hoặc rollback (nếu lỗi)
    public ResourceResponseDTO createResource(ResourceUploadRequest request, User uploader) {

        // --- BƯỚC 1: Xác thực dữ liệu đầu vào ---

        // 1a. Lấy các Entity liên quan (Topic, Type, Status)
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("error.topic.notfound")); // i18n key

        ResourceType type = resourceTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("error.type.notfound"));

        ResourceStatus status = resourceStatusRepository.findByName(DEFAULT_STATUS)
                .orElseThrow(() -> new ResourceNotFoundException("error.status.notfound")); // i18n key

        // 1b. Kiểm tra xem user upload file hay link YouTube
        MultipartFile file = request.getFile();
        String youtubeUrl = request.getYoutubeUrl();
        String filePath = null;
        String originalFileName = null;
        Long fileSize = null;

        if (file != null && !file.isEmpty()) {
            // --- BƯỚC 2A: Xử lý Upload File ---
            try {
                // Gọi MinioService để upload
                filePath = minioService.uploadFile(file, MINIO_FOLDER);
                originalFileName = file.getOriginalFilename();
                fileSize = file.getSize();
            } catch (Exception e) {
                throw new FileUploadException("error.minio.upload", e); // i18n key
            }
        } else if (youtubeUrl != null && !youtubeUrl.isBlank()) {
            // --- BƯỚC 2B: Xử lý Link YouTube ---
            filePath = null; // Không có file path vì là link ngoài
        } else {
            // Nếu không cung cấp cả hai
            throw new ValidationException("error.resource.missingfile"); // i18n key
        }

        // --- BƯỚC 3: Tạo và Lưu Entity ---
        Resource resource = new Resource();
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setUploader(uploader); // Gán người upload
        resource.setTopic(topic);
        resource.setType(type);
        resource.setStatus(status); // Mặc định là PENDING

        // Lưu thông tin file/link
        resource.setFilePath(filePath); // Path trên MinIO (hoặc null)
        resource.setOriginalFileName(originalFileName);
        resource.setFileSize(fileSize);
        resource.setYoutubeUrl(youtubeUrl); // Link YouTube (hoặc null)

        // Lưu vào Database
        Resource savedResource = resourceRepository.save(resource);

        // --- BƯỚC 4: Map Entity sang DTO để trả về ---
        return mapToResponseDTO(savedResource);
    }

    @Override
    @Transactional(readOnly = true) // Đặt readOnly = true để tối ưu hiệu năng đọc
    public Page<ResourceResponseDTO> getPublicResources(Long topicId, String searchQuery, Pageable pageable) {

        // 1. Lấy Entity Trạng thái "APPROVED"
        ResourceStatus approvedStatus = resourceStatusRepository.findByName(PUBLIC_STATUS)
                .orElseThrow(() -> new ResourceNotFoundException("error.status.notfound.approved")); // i18n

        // 2. Lấy Entity Topic (nếu có lọc)
        Topic topic = null;
        if (topicId != null) {
            topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new ResourceNotFoundException("error.topic.notfound"));
        }

        // 3. Xử lý logic lọc
        Page<Resource> resourcePage;
        boolean hasSearch = StringUtils.isNotBlank(searchQuery);

        if (topic != null && hasSearch) {
            // Lọc theo cả Topic và Search
            resourcePage = resourceRepository.findByStatusAndTopicAndTitleContainingIgnoreCase(approvedStatus, topic, searchQuery, pageable);
        } else if (topic != null) {
            // Chỉ lọc theo Topic
            resourcePage = resourceRepository.findByStatusAndTopic(approvedStatus, topic, pageable);
        } else if (hasSearch) {
            // Chỉ lọc theo Search
            resourcePage = resourceRepository.findByStatusAndTitleContainingIgnoreCase(approvedStatus, searchQuery, pageable);
        } else {
            // Không lọc gì cả, lấy tất cả file APPROVED
            resourcePage = resourceRepository.findByStatus(approvedStatus, pageable);
        }

        // 4. Map Page<Entity> sang Page<DTO> và trả về
        // .map(this::mapToResponseDTO) sẽ tự động gọi hàm mapToResponseDTO cho từng item
        return resourcePage.map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceResponseDTO getPublicResourceById(Long id) {
        // 1. Lấy Entity Trạng thái "APPROVED"
        ResourceStatus approvedStatus = resourceStatusRepository.findByName(PUBLIC_STATUS)
                .orElseThrow(() -> new ResourceNotFoundException("error.status.notfound.approved"));

        // 2. Tìm tài liệu theo ID và phải là APPROVED
        Resource resource = resourceRepository.findByIdAndStatus(id, approvedStatus)
                .orElseThrow(() -> new ResourceNotFoundException("error.resource.notfound.public")); // i18n

        // 3. Map sang DTO và trả về
        return mapToResponseDTO(resource);
    }

    @Override
    @Transactional
    public ResourceResponseDTO approveResource(Long id, User approver) {
        // 1. Tìm tài liệu
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.resource.notfound"));

        // 2. Lấy trạng thái "APPROVED"
        ResourceStatus approvedStatus = resourceStatusRepository.findByName(APPROVED_STATUS)
                .orElseThrow(() -> new ResourceNotFoundException("error.status.notfound.approved"));

        // 3. Cập nhật thông tin
        resource.setStatus(approvedStatus);
        resource.setApprover(approver); // Gán người duyệt
        resource.setApprovedAt(Instant.now()); // Gán thời gian duyệt

        // 4. Lưu
        Resource savedResource = resourceRepository.save(resource);
        return mapToResponseDTO(savedResource);
    }

    @Override
    @Transactional
    public ResourceResponseDTO updateResource(Long id, ResourceUpdateRequest request) {
        // 1. Tìm tài liệu (Quyền đã được kiểm tra ở Controller)
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.resource.notfound"));

        // 2. Tìm các Entity liên quan (Topic, Type)
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("error.topic.notfound"));

        ResourceType type = resourceTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("error.type.notfound"));

        // 3. Cập nhật metadata
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setTopic(topic);
        resource.setType(type);
        // (Chúng ta không cho phép sửa file hoặc link YouTube ở đây)

        // 4. Lưu và trả về
        Resource updatedResource = resourceRepository.save(resource);
        return mapToResponseDTO(updatedResource);
    }

    @Override
    @Transactional
    public void deleteResource(Long id) {
        // 1. Tìm tài liệu (Quyền đã được kiểm tra ở Controller)
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.resource.notfound"));

        // 2. (Quan trọng) Xóa file trên MinIO trước
        String filePath = resource.getFilePath();
        if (filePath != null && !filePath.isBlank()) {
            try {
                minioService.deleteFile(filePath);
            } catch (Exception e) {
                // Ghi log lỗi nhưng vẫn tiếp tục xóa DB
                // (Bạn có thể ném FileUploadException nếu muốn dừng hẳn)
                System.err.println("Không thể xóa file trên MinIO: " + filePath + ". Lỗi: " + e.getMessage());
            }
        }

        // 3. Xóa metadata trong Database
        resourceRepository.delete(resource);
    }

    /**
     * Helper (private): Chuyển đổi Resource (Entity) sang ResourceResponseDTO (DTO).
     * Đây là bước quan trọng để lồng DTO, tránh lộ Entity.
     */
    private ResourceResponseDTO mapToResponseDTO(Resource resource) {
        if (resource == null) return null;

        ResourceResponseDTO dto = new ResourceResponseDTO();
        dto.setId(resource.getId());
        dto.setTitle(resource.getTitle());
        dto.setDescription(resource.getDescription());

        // Dùng MinioService để tạo Full URL (nếu là file)
        if (resource.getFilePath() != null) {
            dto.setFileUrl(minioService.getFileUrl(resource.getFilePath()));
        }

        dto.setOriginalFileName(resource.getOriginalFileName());
        dto.setFileSize(resource.getFileSize());
        dto.setYoutubeUrl(resource.getYoutubeUrl());
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setApprovedAt(resource.getApprovedAt());

        // Map các Entity lồng nhau sang DTO lồng nhau

        // Map Uploader
        if (resource.getUploader() != null) {
            UserResponseDTO uploaderDto = new UserResponseDTO();
            uploaderDto.setId(resource.getUploader().getId());
            uploaderDto.setFullName(resource.getUploader().getFullName());
            uploaderDto.setEmail(resource.getUploader().getEmail());
            dto.setUploader(uploaderDto);
        }

        // Map Approver (nếu có)
        if (resource.getApprover() != null) {
            UserResponseDTO approverDto = new UserResponseDTO();
            approverDto.setId(resource.getApprover().getId());
            approverDto.setFullName(resource.getApprover().getFullName());
            approverDto.setEmail(resource.getApprover().getEmail());
            dto.setApprover(approverDto);
        }

        // Map Topic
        TopicResponseDTO topicDto = new TopicResponseDTO();
        topicDto.setId(resource.getTopic().getId());
        topicDto.setName(resource.getTopic().getName());
        topicDto.setSlug(resource.getTopic().getSlug());
        dto.setTopic(topicDto);

        // Map Type
        ResourceTypeResponseDTO typeDto = new ResourceTypeResponseDTO();
        typeDto.setId(resource.getType().getId());
        typeDto.setName(resource.getType().getName());
        typeDto.setSlug(resource.getType().getSlug());
        dto.setType(typeDto);

        // Map Status
        ResourceStatusResponseDTO statusDto = new ResourceStatusResponseDTO();
        statusDto.setId(resource.getStatus().getId());
        statusDto.setName(resource.getStatus().getName());
        dto.setStatus(statusDto);

        return dto;
    }
}

