package com.khohoclieumnhongphong.khohoclieu.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception này được throw khi không tìm thấy tài nguyên (ví dụ: User, Resource, Topic)
 * Annotation @ResponseStatus(HttpStatus.NOT_FOUND) sẽ tự động
 * trả về HTTP Status 404 Not Found mỗi khi Exception này được throw.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}