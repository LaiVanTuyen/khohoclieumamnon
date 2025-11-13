package com.khohoclieumnhongphong.khohoclieu.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception này được throw khi có lỗi xảy ra trong quá trình
 * upload hoặc xử lý file (ví dụ: lỗi kết nối MinIO).
 * Trả về 500 Internal Server Error.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FileUploadException extends RuntimeException {

    public FileUploadException(String message) {
        super(message);
    }

    // Constructor này hữu ích khi bạn muốn bọc (wrap) một lỗi gốc từ MinIO
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
