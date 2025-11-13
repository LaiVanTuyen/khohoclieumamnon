package com.khohoclieumnhongphong.khohoclieu.repository;

import com.khohoclieumnhongphong.khohoclieu.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    // Lấy tất cả banner đang active, sắp xếp theo ngày tạo mới nhất
    List<Banner> findByIsActiveTrueOrderByCreatedAtDesc();

    // Lấy tất cả banner, sắp xếp theo ngày tạo mới nhất
    List<Banner> findAllByOrderByCreatedAtDesc();
}