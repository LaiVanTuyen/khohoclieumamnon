package com.khohoclieumnhongphong.khohoclieu.service.impl;

import com.khohoclieumnhongphong.khohoclieu.dto.resource.CategoryRequestDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.topic.TopicResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.entity.Topic;
import com.khohoclieumnhongphong.khohoclieu.exception.ResourceNotFoundException;
import com.khohoclieumnhongphong.khohoclieu.repository.TopicRepository;
import com.khohoclieumnhongphong.khohoclieu.service.ITopicService;
import com.khohoclieumnhongphong.khohoclieu.util.SlugUtil; // Import tiện ích vừa tạo
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements ITopicService {

    private final TopicRepository topicRepository;

    // Helper (private) để map Entity sang DTO
    private TopicResponseDTO mapToDTO(Topic topic) {
        TopicResponseDTO dto = new TopicResponseDTO();
        dto.setId(topic.getId());
        dto.setName(topic.getName());
        dto.setSlug(topic.getSlug());
        dto.setDescription(topic.getDescription());
        dto.setCreatedAt(topic.getCreatedAt());
        return dto;
    }

    @Override
    public List<TopicResponseDTO> getAllTopics() {
        return topicRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TopicResponseDTO getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.topic.notfound")); // i18n
        return mapToDTO(topic);
    }

    @Override
    public TopicResponseDTO createTopic(CategoryRequestDTO request) {
        // 1. Tạo slug
        String slug = SlugUtil.toSlug(request.getName());

        // 2. Kiểm tra slug đã tồn tại chưa
        if (topicRepository.existsBySlug(slug)) {
            throw new ValidationException("error.topic.slug.exists"); // i18n
        }

        // 3. Tạo Entity
        Topic newTopic = new Topic();
        newTopic.setName(request.getName());
        newTopic.setSlug(slug);
        newTopic.setDescription(request.getDescription());

        // 4. Lưu và trả về DTO
        Topic savedTopic = topicRepository.save(newTopic);
        return mapToDTO(savedTopic);
    }

    @Override
    public TopicResponseDTO updateTopic(Long id, CategoryRequestDTO request) {
        // 1. Tìm Topic
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.topic.notfound"));

        // 2. Tạo slug mới
        String newSlug = SlugUtil.toSlug(request.getName());

        // 3. Kiểm tra slug mới có bị trùng với một Topic KHÁC không
        topicRepository.findBySlug(newSlug).ifPresent(existingTopic -> {
            if (!existingTopic.getId().equals(id)) {
                throw new ValidationException("error.topic.slug.exists"); // i18n
            }
        });

        // 4. Cập nhật thông tin
        topic.setName(request.getName());
        topic.setSlug(newSlug);
        topic.setDescription(request.getDescription());

        // 5. Lưu và trả về
        Topic updatedTopic = topicRepository.save(topic);
        return mapToDTO(updatedTopic);
    }

    @Override
    public void deleteTopic(Long id) {
        // 1. Kiểm tra xem Topic có tồn tại không
        if (!topicRepository.existsById(id)) {
            throw new ResourceNotFoundException("error.topic.notfound");
        }

        try {
            // 2. Xóa
            topicRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // 3. Bắt lỗi nếu Topic này đang được Resource sử dụng (nhờ ON DELETE RESTRICT)
            throw new ValidationException("error.topic.inuse"); // i18n
        }
    }
}