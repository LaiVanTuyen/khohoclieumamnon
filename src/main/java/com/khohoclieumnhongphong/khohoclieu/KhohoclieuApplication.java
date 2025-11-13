package com.khohoclieumnhongphong.khohoclieu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Bật tính năng tự động theo dõi thời gian tạo và cập nhật bản ghi
public class KhohoclieuApplication {

    public static void main(String[] args) {
        SpringApplication.run(KhohoclieuApplication.class, args);
    }

}
