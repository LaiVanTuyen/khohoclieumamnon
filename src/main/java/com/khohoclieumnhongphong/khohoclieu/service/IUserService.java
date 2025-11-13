package com.khohoclieumnhongphong.khohoclieu.service; // (Giữ package của bạn)

import com.khohoclieumnhongphong.khohoclieu.dto.user.UserCreateRequest;
import com.khohoclieumnhongphong.khohoclieu.dto.user.UserUpdateRequest;
import com.khohoclieumnhongphong.khohoclieu.dto.user.AdminUserResponseDTO;

import java.util.List;

public interface IUserService {

    List<AdminUserResponseDTO> getAllUsers();

    AdminUserResponseDTO getUserById(Long id);

    AdminUserResponseDTO createUser(UserCreateRequest request);

    AdminUserResponseDTO updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);
}