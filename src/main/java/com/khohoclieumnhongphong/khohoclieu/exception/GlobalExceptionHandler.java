package com.khohoclieumnhongphong.khohoclieu.exception;

import org.springframework.beans.factory.annotation.Autowired; // <-- Thêm
import org.springframework.context.MessageSource; // <-- Thêm
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException; // <-- Thêm
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Locale; // <-- Thêm

@RestControllerAdvice // Annotation này cho phép xử lý exception tập trung
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource; // <-- Inject MessageSource để dịch lỗi

    /**
     * Bắt lỗi ResourceNotFoundException
     * (Đã cập nhật để dùng i18n)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        // Lấy ngôn ngữ (vi/en) từ request
        Locale locale = request.getLocale();

        // Dịch key lỗi (ví dụ: "resource.notfound") sang message tương ứng
        String errorMessage = messageSource.getMessage(ex.getMessage(), null, locale);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                Instant.now(),
                errorMessage, // Message đã được dịch
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Bắt lỗi FileUploadException
     * (Đã cập nhật để dùng i18n)
     */
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadException(
            FileUploadException ex, WebRequest request) {

        Locale locale = request.getLocale();
        String errorMessage = messageSource.getMessage(ex.getMessage(), null, locale);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now(),
                errorMessage, // Message đã được dịch
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Bắt lỗi Validation (cho các DTO dùng @Valid, @NotBlank, @Email...)
     * (Handler này không cần sửa vì message đã được i18n giải quyết
     * nhờ LocalValidatorFactoryBean)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        // Lấy lỗi validation đầu tiên. Message này đã được i18n dịch sẵn
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                Instant.now(),
                errorMessage, // Ví dụ: "Email không được để trống" (đã dịch)
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Bắt lỗi BadCredentialsException (sai email hoặc password)
     * Trả về 401 UNAUTHORIZED thay vì 500
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {

        Locale locale = request.getLocale();
        String errorMessage = messageSource.getMessage("error.auth.badcredentials", null, locale);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                Instant.now(),
                errorMessage, // "Email hoặc mật khẩu không chính xác" hoặc "Invalid email or password"
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Bắt tất cả các lỗi chung khác (catch-all)
     * (Đã cập nhật để dùng i18n)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        Locale locale = request.getLocale();
        // Cung cấp một key i18n chung cho các lỗi không xác định
        String errorMessage = messageSource.getMessage("error.unknown", null, locale);

        // (Tùy chọn) Ghi lại lỗi đầy đủ vào log
        // log.error("Unknown error occurred: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now(),
                errorMessage + ": " + ex.getMessage(), // Hiển thị lỗi đã dịch và lỗi gốc
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}