package com.khohoclieumnhongphong.khohoclieu.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data
@AllArgsConstructor // Tạo constructor với tất cả các trường
public class ErrorResponse {
    private int statusCode;
    private Instant timestamp;
    private String message;
    private String path; // URI của request gây ra lỗi
}
