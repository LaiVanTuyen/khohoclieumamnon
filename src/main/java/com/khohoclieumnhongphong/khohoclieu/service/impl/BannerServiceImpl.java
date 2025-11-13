package com.khohoclieumnhongphong.khohoclieu.service.impl;

import com.khohoclieumnhongphong.khohoclieu.dto.banner.BannerResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.banner.BannerSaveRequest;
import com.khohoclieumnhongphong.khohoclieu.entity.Banner;
import com.khohoclieumnhongphong.khohoclieu.exception.FileUploadException;
import com.khohoclieumnhongphong.khohoclieu.exception.ResourceNotFoundException;
import com.khohoclieumnhongphong.khohoclieu.repository.BannerRepository;
import com.khohoclieumnhongphong.khohoclieu.service.IBannerService;
import com.khohoclieumnhongphong.khohoclieu.service.IMinioService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements IBannerService {

    private final BannerRepository bannerRepository;
    private final IMinioService minioService;
    private static final String BANNER_FOLDER = "banners";

    // Helper (private) map Entity sang DTO
    private BannerResponseDTO mapToDTO(Banner banner) {
        BannerResponseDTO dto = new BannerResponseDTO();
        dto.setId(banner.getId());
        dto.setTitle(banner.getTitle());
        // Lấy full URL từ MinIO
        dto.setImageUrl(minioService.getFileUrl(banner.getImagePath()));
        dto.setLinkUrl(banner.getLinkUrl());
        dto.setActive(banner.isActive());
        dto.setCreatedAt(banner.getCreatedAt());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDTO> getActiveBanners() {
        return bannerRepository.findByIsActiveTrueOrderByCreatedAtDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDTO> getAllBanners() {
        return bannerRepository.findAllByOrderByCreatedAtDesc().stream() // Giả sử bạn thêm hàm này vào Repo
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BannerResponseDTO getBannerById(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.banner.notfound"));
        return mapToDTO(banner);
    }

    @Override
    @Transactional
    public BannerResponseDTO createBanner(BannerSaveRequest request) {
        // 1. Kiểm tra file
        if (request.getImage() == null || request.getImage().isEmpty()) {
            throw new ValidationException("error.image.notnull"); // i18n
        }

        // 2. Upload ảnh lên MinIO
        String imagePath;
        try {
            imagePath = minioService.uploadFile(request.getImage(), BANNER_FOLDER);
        } catch (Exception e) {
            throw new FileUploadException("error.minio.upload", e);
        }

        // 3. Tạo Entity
        Banner newBanner = new Banner();
        newBanner.setTitle(request.getTitle());
        newBanner.setImagePath(imagePath); // Lưu path từ MinIO
        newBanner.setLinkUrl(request.getLinkUrl());
        newBanner.setActive(request.isActive());

        // 4. Lưu vào DB
        Banner savedBanner = bannerRepository.save(newBanner);
        return mapToDTO(savedBanner);
    }

    @Override
    @Transactional
    public BannerResponseDTO updateBanner(Long id, BannerSaveRequest request) {
        // 1. Tìm banner
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.banner.notfound"));

        MultipartFile newImage = request.getImage();

        // 2. Kiểm tra xem có upload ảnh MỚI không
        if (newImage != null && !newImage.isEmpty()) {

            // 2a. Xóa ảnh CŨ trên MinIO
            try {
                minioService.deleteFile(banner.getImagePath());
            } catch (Exception e) {
                // Ghi log lỗi nhưng vẫn tiếp tục (lỗi không xóa được file cũ)
                System.err.println("Không thể xóa file MinIO cũ: " + banner.getImagePath());
            }

            // 2b. Upload ảnh MỚI
            String newImagePath;
            try {
                newImagePath = minioService.uploadFile(newImage, BANNER_FOLDER);
                banner.setImagePath(newImagePath); // Cập nhật path mới
            } catch (Exception e) {
                throw new FileUploadException("error.minio.upload", e);
            }
        }

        // 3. Cập nhật các thông tin khác
        banner.setTitle(request.getTitle());
        banner.setLinkUrl(request.getLinkUrl());
        banner.setActive(request.isActive());

        // 4. Lưu và trả về
        Banner updatedBanner = bannerRepository.save(banner);
        return mapToDTO(updatedBanner);
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        // 1. Tìm banner
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.banner.notfound"));

        // 2. (Quan trọng) Xóa file trên MinIO
        try {
            minioService.deleteFile(banner.getImagePath());
        } catch (Exception e) {
            // Lỗi xóa file, ném exception
            throw new FileUploadException("error.minio.delete", e);
        }

        // 3. Xóa trong DB
        bannerRepository.delete(banner);
    }
}