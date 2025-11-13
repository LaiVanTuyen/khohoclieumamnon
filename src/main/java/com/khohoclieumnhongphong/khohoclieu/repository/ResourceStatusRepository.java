package com.khohoclieumnhongphong.khohoclieu.repository;

import com.khohoclieumnhongphong.khohoclieu.entity.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceStatusRepository extends JpaRepository<ResourceStatus, Long> {
    Optional<ResourceStatus> findByName(String name);
}