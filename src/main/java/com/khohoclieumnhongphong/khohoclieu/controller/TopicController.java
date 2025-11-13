package com.khohoclieumnhongphong.khohoclieu.controller; // (Giữ package của bạn)

import com.khohoclieumnhongphong.khohoclieu.dto.resource.CategoryRequestDTO;
import com.khohoclieumnhongphong.khohoclieu.dto.topic.TopicResponseDTO;
import com.khohoclieumnhongphong.khohoclieu.service.ITopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("topics")
@RequiredArgsConstructor
@Tag(name = "3. Topic API", description = "API quản lý Chủ đề")
public class TopicController {

    private final ITopicService topicService;

    // --- API CÔNG KHAI (PUBLIC) ---

    @GetMapping
    @Operation(summary = "Lấy tất cả chủ đề (Public)")
    public ResponseEntity<List<TopicResponseDTO>> getAllTopics() {
        List<TopicResponseDTO> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết một chủ đề (Public)")
    public ResponseEntity<TopicResponseDTO> getTopicById(@PathVariable Long id) {
        TopicResponseDTO topic = topicService.getTopicById(id);
        return ResponseEntity.ok(topic);
    }

    // --- API BẢO MẬT (ADMIN) ---

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới được tạo
    @Operation(summary = "Tạo chủ đề mới (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TopicResponseDTO> createTopic(@Valid @RequestBody CategoryRequestDTO request) {
        TopicResponseDTO newTopic = topicService.createTopic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTopic);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới được sửa
    @Operation(summary = "Cập nhật chủ đề (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TopicResponseDTO> updateTopic(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO request) {
        TopicResponseDTO updatedTopic = topicService.updateTopic(id, request);
        return ResponseEntity.ok(updatedTopic);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới được xóa
    @Operation(summary = "Xóa chủ đề (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content
    }
}