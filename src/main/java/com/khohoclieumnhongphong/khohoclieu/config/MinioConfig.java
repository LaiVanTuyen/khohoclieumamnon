package com.khohoclieumnhongphong.khohoclieu.config; // (Hãy đảm bảo package là đúng)

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    // Đọc giá trị 'url' từ trong 'minio' (application.yml)
    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    /**
     * Tạo Bean MinioClient
     * Bean này sẽ được @Autowired trong MinioServiceImpl
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }
}