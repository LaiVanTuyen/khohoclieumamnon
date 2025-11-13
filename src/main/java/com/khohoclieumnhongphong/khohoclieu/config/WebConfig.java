package com.khohoclieumnhongphong.khohoclieu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Cấu hình CORS cho toàn bộ ứng dụng
        registry.addMapping("/api/**") // Áp dụng cho tất cả các đường dẫn /api/
                .allowedOrigins("http://localhost:3000", "http://localhost:3001") // Cho phép các domain này
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // Cho phép các phương thức
                .allowedHeaders("*") // Cho phép tất cả các header
                .allowCredentials(true) // Cho phép gửi cookie/token
                .maxAge(3600); // Thời gian cache pre-flight request
    }
}