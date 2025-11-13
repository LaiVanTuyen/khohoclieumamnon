package com.khohoclieumnhongphong.khohoclieu.repository;


import com.khohoclieumnhongphong.khohoclieu.entity.ERole;
import com.khohoclieumnhongphong.khohoclieu.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}