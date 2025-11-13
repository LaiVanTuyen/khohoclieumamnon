package com.khohoclieumnhongphong.khohoclieu.service;

import com.khohoclieumnhongphong.khohoclieu.dto.resource.CategoryRequestDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.topic.TopicResponseDTO;

import java.util.List;

public interface ITopicService {

    // Lấy tất cả chủ đề (cho public)
    List<TopicResponseDTO> getAllTopics();

    // Lấy một chủ đề theo ID (cho public)
    TopicResponseDTO getTopicById(Long id);

    // Tạo chủ đề mới (cho admin)
    TopicResponseDTO createTopic(CategoryRequestDTO request);

    // Cập nhật chủ đề (cho admin)
    TopicResponseDTO updateTopic(Long id, CategoryRequestDTO request);

    // Xóa chủ đề (cho admin)
    void deleteTopic(Long id);
}