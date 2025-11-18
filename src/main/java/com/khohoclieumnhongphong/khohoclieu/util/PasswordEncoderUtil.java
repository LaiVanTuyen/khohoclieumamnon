package com.khohoclieumnhongphong.khohoclieu.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class để tạo mật khẩu BCrypt
 * Chạy class này để tạo hash cho mật khẩu mới
 */
public class PasswordEncoderUtil {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Tạo hash cho mật khẩu "admin123"
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("=== BCrypt Password Generator ===");
        System.out.println("Raw Password: " + rawPassword);
        System.out.println("Encoded Password: " + encodedPassword);
        System.out.println("Length: " + encodedPassword.length());
        System.out.println();

        // Test verify
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("Password matches: " + matches);

        System.out.println("\n=== SQL Update Statement ===");
        System.out.println("UPDATE users SET password = '" + encodedPassword + "' WHERE email = 'admin@khohoclieu.com';");
    }
}

