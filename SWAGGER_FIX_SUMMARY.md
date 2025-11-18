# Swagger UI - Hướng dẫn sử dụng

## Vấn đề đã được sửa

Lỗi 500 khi truy cập `/api/v1/v3/api-docs` và Swagger UI đã được khắc phục bằng cách:

1. **Loại bỏ tiền tố `/api/v1` trong controllers** - Vì `server.servlet.context-path=/api/v1` đã tự động thêm tiền tố, việc hardcode `/api/v1` trong `@RequestMapping` gây ra double-prefix và lỗi.

2. **Xóa SwaggerUiController** - SpringDoc tự động xử lý redirect, không cần controller tùy chỉnh.

3. **Cấu hình SpringDoc** - Đã thiết lập đúng path cho api-docs và swagger-ui trong `application.yml`.

4. **Cấu hình Security** - Đã permit các đường dẫn Swagger/OpenAPI trong `SecurityConfig`.

5. **Resource Handlers** - Đã thêm mapping cho webjars và swagger-ui resources trong `WebConfig`.

## Cách sử dụng Swagger UI

### 1. Khởi động ứng dụng

```powershell
# Cách 1: Chạy bằng Maven
.\mvnw.cmd spring-boot:run

# Cách 2: Chạy JAR đã build
java -jar target\Preschool_Data_Warehouse-0.0.1-SNAPSHOT.jar
```

### 2. Truy cập Swagger UI

Mở trình duyệt và truy cập một trong các URL sau:

**Swagger UI (Giao diện):**
- http://localhost:8080/api/v1/swagger-ui/index.html ✅ (Khuyến nghị)
- http://localhost:8080/api/v1/swagger-ui.html

**API Documentation (JSON):**
- http://localhost:8080/api/v1/v3/api-docs

### 3. Test nhanh bằng PowerShell

```powershell
# Kiểm tra API docs (JSON)
Invoke-RestMethod 'http://localhost:8080/api/v1/v3/api-docs' | ConvertTo-Json

# Kiểm tra Swagger UI
Invoke-WebRequest 'http://localhost:8080/api/v1/swagger-ui/index.html' -UseBasicParsing
```

## Cấu trúc URL API

Vì `context-path=/api/v1`, tất cả endpoints đều có tiền tố `/api/v1`:

- `/api/v1/auth/**` - Authentication (Public)
- `/api/v1/banners/**` - Banner management
- `/api/v1/resources/**` - Resource management
- `/api/v1/topics/**` - Topic management
- `/api/v1/types/**` - Resource type management
- `/api/v1/users/**` - User management (Admin only)

## Các thay đổi chính

### SecurityConfig.java
```java
.requestMatchers(
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/webjars/**",
    "/api/v1/swagger-ui/**",
    "/api/v1/swagger-ui.html",
    "/api/v1/swagger-ui/index.html",
    "/api/v1/webjars/**",
    "/api/v1/v3/api-docs/**"
).permitAll()
```

### application.yml
```yaml
server:
  servlet:
    context-path: /api/v1

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
  packages-to-scan: com.khohoclieumnhongphong.khohoclieu.controller
```

### Controllers (ví dụ)
```java
// ❌ SAI - Double prefix
@RequestMapping("/api/v1/banners")

// ✅ ĐÚNG - Context-path tự động thêm /api/v1
@RequestMapping("/banners")
```

## Xử lý lỗi thường gặp

### Lỗi 404 - Not Found
- Kiểm tra ứng dụng đã khởi động chưa
- Đảm bảo port 8080 không bị chiếm
- Kiểm tra URL có đúng tiền tố `/api/v1` không

### Lỗi 500 - Internal Server Error
- Kiểm tra logs để xem stacktrace
- Đảm bảo database đang chạy (MySQL port 3307)
- Kiểm tra MinIO service (port 9000)

### Swagger UI hiển thị trắng
- Xóa cache trình duyệt (Ctrl+Shift+Del)
- Thử mở ở chế độ ẩn danh
- Kiểm tra console của trình duyệt (F12)

## Ghi chú

- **Log level** hiện tại đang ở DEBUG cho springdoc - có thể giảm xuống INFO trong production
- **CORS** đã được cấu hình cho localhost:3000 và localhost:3001
- **JWT Authentication** được yêu cầu cho hầu hết endpoints (trừ public APIs)

---

**Ngày sửa:** 18/11/2025
**Phiên bản:** 0.0.1-SNAPSHOT

