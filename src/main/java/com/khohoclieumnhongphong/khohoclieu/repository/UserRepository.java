package com.khohoclieumnhongphong.khohoclieu.repository;

import com.khohoclieumnhongphong.khohoclieu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA tự động hiểu: "tìm một User bằng cột email"
    Optional<User> findByEmail(String email);

    // Kiểm tra xem email đã tồn tại chưa
    Boolean existsByEmail(String email);
}
