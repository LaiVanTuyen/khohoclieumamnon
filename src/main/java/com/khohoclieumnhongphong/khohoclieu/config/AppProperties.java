package com.khohoclieumnhongphong.khohoclieu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app") // Đọc tất cả cấu hình có tiền tố "app"
@Data
public class AppProperties {

    // Sẽ đọc từ app.jwt.secret, app.jwt.expiration-ms
    private final Jwt jwt = new Jwt();

    @Data
    public static class Jwt {
        private String secret;
        private long expirationMs; // Thời gian hết hạn của token
    }
}