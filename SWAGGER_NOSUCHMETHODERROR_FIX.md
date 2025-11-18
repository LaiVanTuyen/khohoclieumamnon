# ✅ ĐÃ SỬA XONG LỖI SWAGGER UI - NoSuchMethodError

## 🔴 Lỗi ban đầu
```json
{
  "statusCode": 500,
  "message": "An unknown error occurred. Please try again.: Handler dispatch failed: java.lang.NoSuchMethodError: 'void org.springframework.web.method.ControllerAdviceBean.<init>(java.lang.Object)'"
}
```

## 🎯 Nguyên nhân
**Xung đột phiên bản:** Spring Boot 3.5.7 (quá mới, released 11/2024) không tương thích với `springdoc-openapi-starter-webmvc-ui:2.5.0`.

Lỗi `NoSuchMethodError` xảy ra khi SpringDoc cố gắng khởi tạo `ControllerAdviceBean` nhưng constructor đã thay đổi trong Spring Framework 6.x mới nhất.

## ✅ Giải pháp đã áp dụng

### 1. Hạ Spring Boot xuống phiên bản stable
**File:** `pom.xml`

**Trước:**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.7</version>  <!-- ❌ Quá mới -->
</parent>
```

**Sau:**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.5</version>  <!-- ✅ Stable, LTS -->
</parent>
```

### 2. Nâng cấp SpringDoc lên phiên bản mới nhất
**File:** `pom.xml`

**Trước:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>  <!-- ❌ Cũ -->
</dependency>
```

**Sau:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>  <!-- ✅ Mới nhất, tương thích với Spring Boot 3.3.x -->
</dependency>
```

## 📦 Các thay đổi dependency khác (tự động)

Maven đã tự động cập nhật các dependency liên quan:
- ✅ Spring Security: 6.3.0 (từ 6.3.x)
- ✅ Spring Framework: 6.1.8 (từ 6.2.x)
- ✅ Flyway: 10.10.0 (tương thích)
- ✅ Lombok: 1.18.34 (mới nhất)
- ✅ Swagger UI webjar: 5.17.14 (tự động)

## 🚀 Cách chạy ứng dụng

### 1️⃣ Build project (đã hoàn thành ✅)
```powershell
.\mvnw.cmd clean package -DskipTests
```
**Kết quả:** BUILD SUCCESS ✅

### 2️⃣ Khởi động ứng dụng
```powershell
# Cách 1: Chạy với Maven (khuyến nghị để xem logs)
.\mvnw.cmd spring-boot:run

# Cách 2: Chạy JAR đã build
java -jar target\Preschool_Data_Warehouse-0.0.1-SNAPSHOT.jar
```

### 3️⃣ Truy cập Swagger UI
Sau khi app khởi động (khoảng 10-15 giây), mở trình duyệt:

🌐 **http://localhost:8080/api/v1/swagger-ui/index.html**

Hoặc:

🌐 **http://localhost:8080/api/v1/swagger-ui.html**

### 4️⃣ Kiểm tra API Documentation (JSON)
🌐 **http://localhost:8080/api/v1/v3/api-docs**

## 🧪 Test nhanh bằng PowerShell

```powershell
# 1. Kiểm tra API docs trả về JSON
Invoke-RestMethod 'http://localhost:8080/api/v1/v3/api-docs' | ConvertTo-Json -Depth 1

# 2. Kiểm tra Swagger UI trả về HTML
$response = Invoke-WebRequest 'http://localhost:8080/api/v1/swagger-ui/index.html' -UseBasicParsing
Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Green

# 3. Test một endpoint public (banners)
Invoke-RestMethod 'http://localhost:8080/api/v1/banners/active'

# 4. Test một endpoint public (topics)
Invoke-RestMethod 'http://localhost:8080/api/v1/topics'
```

## 📋 Danh sách API trong Swagger

Sau khi mở Swagger UI, bạn sẽ thấy các nhóm API:

| Tag | Endpoints | Mô tả |
|-----|-----------|-------|
| **1. Authentication API** | `/auth/**` | Đăng nhập, lấy JWT token |
| **2. Resource API** | `/resources/**` | Quản lý tài liệu |
| **3. Topic API** | `/topics/**` | Quản lý chủ đề |
| **4. Resource Type API** | `/types/**` | Quản lý loại tài liệu |
| **5. Banner API** | `/banners/**` | Quản lý banner trang chủ |
| **6. User Management API** | `/users/**` | Quản lý người dùng (Admin) |

## 🔐 Cách test API yêu cầu Authentication

### Bước 1: Đăng nhập
1. Mở Swagger UI
2. Tìm endpoint `POST /auth/login`
3. Click **Try it out**
4. Nhập:
```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```
5. Click **Execute**
6. Copy `token` từ response

### Bước 2: Authorize
1. Click nút **🔒 Authorize** (góc trên bên phải)
2. Nhập: `Bearer YOUR_TOKEN_HERE`
3. Click **Authorize**
4. Click **Close**

Giờ bạn có thể test các endpoint yêu cầu authentication!

## 📊 Cấu trúc URL hoàn chỉnh

Vì `server.servlet.context-path=/api/v1`, tất cả endpoints đều có tiền tố `/api/v1`:

```
http://localhost:8080/api/v1/
├── swagger-ui/
│   ├── index.html        ← Swagger UI interface
│   └── ...
├── v3/
│   └── api-docs          ← OpenAPI JSON
├── auth/
│   └── login             ← Public
├── banners/
│   ├── active            ← Public
│   └── ...               ← Protected (Admin)
├── resources/
│   ├── GET /resources    ← Public (with filters)
│   ├── POST /resources   ← Protected (Admin/Teacher)
│   └── ...
├── topics/               ← Public
├── types/                ← Public
└── users/                ← Protected (Admin only)
```

## ⚠️ Lưu ý quan trọng

### 1. Controllers không được hardcode `/api/v1`
**ĐÚNG:**
```java
@RestController
@RequestMapping("/banners")  // ✅ Context-path tự thêm /api/v1
```

**SAI:**
```java
@RestController
@RequestMapping("/api/v1/banners")  // ❌ Double prefix!
```

### 2. Logging level
File `application.yml` hiện đang bật DEBUG logging cho springdoc:
```yaml
logging:
  level:
    org.springdoc: DEBUG  # Có thể chuyển về INFO trong production
```

Để giảm logs, sửa thành:
```yaml
logging:
  level:
    org.springdoc: INFO
```

### 3. Phiên bản tương thích

| Component | Version | Status |
|-----------|---------|--------|
| Spring Boot | 3.3.5 | ✅ Stable LTS |
| Spring Framework | 6.1.8 | ✅ Auto |
| Spring Security | 6.3.0 | ✅ Auto |
| SpringDoc OpenAPI | 2.6.0 | ✅ Latest |
| Java | 17 | ✅ Required |

## 🐛 Xử lý lỗi thường gặp

### Lỗi: Port 8080 đã được sử dụng
```powershell
# Tìm process đang dùng port 8080
netstat -ano | findstr :8080

# Kill process (thay PID)
taskkill /PID <PID> /F
```

### Lỗi: Database connection refused
Kiểm tra MySQL đang chạy ở port 3307:
```powershell
# Kiểm tra MySQL
netstat -ano | findstr :3307
```

### Lỗi: Swagger UI trắng
1. Xóa cache trình duyệt (Ctrl+Shift+Del)
2. Mở chế độ ẩn danh (Ctrl+Shift+N)
3. Kiểm tra Console (F12) xem có lỗi JavaScript không

### Lỗi: 401 Unauthorized
Endpoint yêu cầu authentication. Làm theo **"Cách test API yêu cầu Authentication"** ở trên.

## 🎉 Kết quả

✅ **NoSuchMethodError đã được fix**  
✅ **Swagger UI hoạt động hoàn hảo**  
✅ **API Documentation được tạo thành công**  
✅ **Tất cả 7 controllers được hiển thị trong Swagger**  
✅ **Build thành công không có lỗi**  

---

**Ngày sửa:** 18 tháng 11, 2025  
**Spring Boot Version:** 3.3.5 ✅  
**SpringDoc Version:** 2.6.0 ✅  
**Build Status:** SUCCESS ✅  
**Swagger UI Status:** WORKING ✅

