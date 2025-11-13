package com.khohoclieumnhongphong.khohoclieu.service.impl;

import com.khohoclieumnhongphong.khohoclieu.exception.FileUploadException;
import com.khohoclieumnhongphong.khohoclieu.service.IMinioService;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class MinioServiceImpl implements IMinioService {

    // Inject MinioClient đã được tạo bởi MinioConfig
    @Autowired
    private MinioClient minioClient;

    // Đọc cấu hình từ application.properties
    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}") // (Ví dụ: http://localhost:9000)
    private String minioUrl;

    private static final Logger logger = LoggerFactory.getLogger(MinioServiceImpl.class);

    /**
     * Tự động chạy sau khi service được khởi tạo.
     * Dùng để kiểm tra và tạo bucket nếu nó chưa tồn tại.
     */
    @PostConstruct
    private void checkBucketExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                // Nếu bucket chưa tồn tại, tạo mới
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                logger.info("MinIO bucket '{}' đã được tạo thành công.", bucketName);

                // (Tùy chọn) Set policy cho bucket là public-read
                // Cần cẩn thận với policy này!
                String policyJson = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":\"*\",\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]}]}";
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(bucketName)
                                .config(policyJson)
                                .build()
                );
                logger.info("Đã set policy public-read cho bucket '{}'", bucketName);

            } else {
                logger.info("MinIO bucket '{}' đã tồn tại.", bucketName);
            }
        } catch (Exception e) {
            logger.error("Lỗi khi kiểm tra/tạo MinIO bucket: ", e);
            throw new RuntimeException("Không thể khởi tạo MinIO bucket", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("error.file.empty"); // Key i18n
        }

        try (InputStream inputStream = file.getInputStream()) {
            // 1. Tạo tên file duy nhất
            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = generateUniqueFileName(originalFileName);
            String objectName = subFolder + "/" + uniqueFileName;

            // 2. Chuẩn bị các tham số để upload
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1) // -1 cho part size mặc định
                    .contentType(file.getContentType())
                    .build();

            // 3. Tải file lên MinIO
            minioClient.putObject(args);

            logger.info("File đã được tải lên thành công: {}", objectName);
            return objectName; // Trả về path (object name) để lưu vào DB

        } catch (Exception e) {
            logger.error("Lỗi khi upload file lên MinIO: ", e);
            throw new FileUploadException("error.minio.upload", e); // Key i18n
        }
    }

    @Override
    public void deleteFile(String objectName) {
        if (objectName == null || objectName.isBlank()) {
            return; // Không có gì để xóa
        }

        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();

            minioClient.removeObject(args);
            logger.info("File đã được xóa khỏi MinIO: {}", objectName);

        } catch (Exception e) {
            logger.error("Lỗi khi xóa file trên MinIO: {}", objectName, e);
            throw new FileUploadException("error.minio.delete", e); // Key i18n
        }
    }

    @Override
    public String getFileUrl(String objectName) {
        if (objectName == null || objectName.isBlank()) {
            return null;
        }
        // Ghép URL thủ công (giả định bucket là public)
        return minioUrl + "/" + bucketName + "/" + objectName;
    }

    /**
     * Helper: Tạo tên file duy nhất bằng UUID
     */
    private String generateUniqueFileName(String originalFileName) {
        if (originalFileName == null) {
            originalFileName = "file";
        }
        // Lấy phần mở rộng (ví dụ: .png, .mp4)
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i);
        }
        // Trả về: [UUID].extension
        return UUID.randomUUID().toString() + extension;
    }
}