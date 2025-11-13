package com.khohoclieumnhongphong.khohoclieu.service;

import com.khohoclieumnhongphong.khohoclieu.dto.banner.BannerSaveRequest;
import com.khohoclieumnhongphong.khohoclieu.dto.banner.BannerResponseDTO;

import java.util.List;

public interface IBannerService {

    // Lấy tất cả banner đang active (cho public)
    List<BannerResponseDTO> getActiveBanners();

    // Lấy tất cả banner (cho admin)
    List<BannerResponseDTO> getAllBanners();

    // Lấy 1 banner theo ID
    BannerResponseDTO getBannerById(Long id);

    // Tạo banner mới (cho admin)
    BannerResponseDTO createBanner(BannerSaveRequest request);

    // Cập nhật banner (cho admin)
    BannerResponseDTO updateBanner(Long id, BannerSaveRequest request);

    // Xóa banner (cho admin)
    void deleteBanner(Long id);
}