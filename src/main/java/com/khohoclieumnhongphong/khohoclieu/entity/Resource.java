package com.khohoclieumnhongphong.khohoclieu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(name = "file_path", length = 1024)
    private String filePath; // Path trên MinIO

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "youtube_url")
    private String youtubeUrl;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    // --- CÁC QUAN HỆ ---

    // Quan hệ Nhiều-Một với User (Người tải lên)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    // Quan hệ Nhiều-Một với User (Người duyệt)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id") // nullable = true (mặc định)
    private User approver;

    // Quan hệ Nhiều-Một với Topic
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    // Quan hệ Nhiều-Một với ResourceType
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private ResourceType type;

    // Quan hệ Nhiều-Một với ResourceStatus
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ResourceStatus status;
}