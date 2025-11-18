# ✅ SWAGGER UI ĐÃ HOẠT ĐỘNG!

## 🎉 Kết quả
- ✅ Swagger UI: **HOẠT ĐỘNG**
- ✅ API Documentation: **HOẠT ĐỘNG** 
- ✅ Spring Boot: **3.3.5**
- ✅ SpringDoc OpenAPI: **2.6.0**
- ✅ Lỗi NoSuchMethodError: **ĐÃ SỬA**

## 🌐 URLs để truy cập

### Swagger UI (Giao diện)
```
http://localhost:8080/api/v1/swagger-ui/index.html
```

### API Documentation (JSON)
```
http://localhost:8080/api/v1/v3/api-docs
```

## 📋 Danh sách API có trong Swagger

1. **Authentication API** - `/api/v1/auth/**`
   - POST `/auth/login` - Đăng nhập (Public)

2. **Resource API** - `/api/v1/resources/**`
   - GET `/resources` - Lấy danh sách tài liệu (Public, có filter)
   - POST `/resources` - Tạo tài liệu mới (Admin/Teacher)
   - PUT `/resources/{id}` - Cập nhật tài liệu (Admin/Teacher)
   - DELETE `/resources/{id}` - Xóa tài liệu (Admin/Teacher)

3. **Topic API** - `/api/v1/topics/**`
   - GET `/topics` - Lấy danh sách chủ đề (Public)
   - POST `/topics` - Tạo chủ đề mới (Admin)
   - PUT `/topics/{id}` - Cập nhật chủ đề (Admin)
   - DELETE `/topics/{id}` - Xóa chủ đề (Admin)

4. **Resource Type API** - `/api/v1/types/**`
   - GET `/types` - Lấy danh sách loại tài liệu (Public)
   - POST `/types` - Tạo loại tài liệu (Admin)
   - PUT `/types/{id}` - Cập nhật loại tài liệu (Admin)
   - DELETE `/types/{id}` - Xóa loại tài liệu (Admin)

5. **Banner API** - `/api/v1/banners/**`
   - GET `/banners/active` - Lấy banner đang hoạt động (Public)
   - GET `/banners` - Lấy tất cả banner (Admin)
   - POST `/banners` - Tạo banner mới (Admin)
   - PUT `/banners/{id}` - Cập nhật banner (Admin)
   - DELETE `/banners/{id}` - Xóa banner (Admin)

6. **User Management API** - `/api/v1/users/**`
   - GET `/users` - Lấy danh sách user (Admin)
   - POST `/users` - Tạo user mới (Admin)
   - PUT `/users/{id}` - Cập nhật user (Admin)
   - DELETE `/users/{id}` - Xóa user (Admin)

## 🔐 Cách test API yêu cầu Authentication

### Bước 1: Đăng nhập
1. Mở Swagger UI
2. Tìm **"1. Authentication API"**
3. Mở endpoint `POST /auth/login`
4. Click **"Try it out"**
5. Nhập request body:
```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```
6. Click **"Execute"**
7. Copy giá trị `token` từ response

### Bước 2: Authorize
1. Click nút **🔒 Authorize** (góc trên bên phải)
2. Nhập vào ô "Value": `Bearer YOUR_TOKEN_HERE`
   - Ví dụ: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
3. Click **"Authorize"**
4. Click **"Close"**

### Bước 3: Test API protected
Giờ bạn có thể test các endpoint yêu cầu authentication như:
- POST `/resources`
- PUT `/resources/{id}`
- GET `/users`
- v.v.

## 🚀 Cách chạy lại app

### Dừng app hiện tại
```powershell
Get-Process java | Stop-Process -Force
```

### Khởi động lại
```powershell
# Cách 1: Chạy với Maven (xem logs)
.\mvnw.cmd spring-boot:run

# Cách 2: Chạy JAR
java -jar target\Preschool_Data_Warehouse-0.0.1-SNAPSHOT.jar
```

### Rebuild sau khi sửa code
```powershell
.\mvnw.cmd clean package -DskipTests
java -jar target\Preschool_Data_Warehouse-0.0.1-SNAPSHOT.jar
```

## 🐛 Xử lý lỗi

### Lỗi: Port 8080 đã được sử dụng
```powershell
# Tìm process đang dùng port 8080
netstat -ano | findstr :8080

# Kill process (thay <PID>)
taskkill /PID <PID> /F
```

### Lỗi: Connection refused (MySQL)
Kiểm tra MySQL đang chạy ở port 3307:
```powershell
netstat -ano | findstr :3307
```
Nếu không chạy, khởi động MySQL service.

### Lỗi: 401 Unauthorized
Endpoint yêu cầu authentication. Làm theo hướng dẫn **"Cách test API yêu cầu Authentication"** ở trên.

### Lỗi: Swagger UI trắng
1. Xóa cache trình duyệt (Ctrl+Shift+Del)
2. Mở ở chế độ ẩn danh (Ctrl+Shift+N)
3. Kiểm tra Console (F12) xem có lỗi không

## 📝 Tóm tắt các thay đổi đã thực hiện

### 1. Downgrade Spring Boot
- **Từ:** 3.5.7 (quá mới, có bug)
- **Xuống:** 3.3.5 (stable, LTS)

### 2. Upgrade SpringDoc
- **Từ:** 2.5.0
- **Lên:** 2.6.0 (tương thích với Spring Boot 3.3.x)

### 3. Sửa BannerController
- **Trước:** `@RequestMapping("/api/v1/banners")` ❌
- **Sau:** `@RequestMapping("/banners")` ✅
- **Lý do:** `context-path=/api/v1` đã tự thêm tiền tố

### 4. Xóa SwaggerUiController
- SpringDoc tự động xử lý redirect, không cần controller tùy chỉnh

### 5. Cấu hình SecurityConfig
- Permit các đường dẫn Swagger: `/swagger-ui/**`, `/v3/api-docs/**`, `/webjars/**`

### 6. Cấu hình WebConfig
- Thêm Resource Handlers cho webjars và swagger-ui

## 🎓 Các quy tắc quan trọng

### ⚠️ KHÔNG hardcode `/api/v1` trong controllers
**ĐÚNG:**
```java
@RestController
@RequestMapping("/banners")  // ✅ Context-path tự thêm /api/v1
public class BannerController { ... }
```

**SAI:**
```java
@RestController
@RequestMapping("/api/v1/banners")  // ❌ Double prefix!
public class BannerController { ... }
```

### 📌 Context-path hoạt động như thế nào?
```yaml
# application.yml
server:
  servlet:
    context-path: /api/v1
```

Với config này:
- Controller mapping: `/banners`
- URL thực tế: `/api/v1/banners` ✅
- Swagger UI: `/api/v1/swagger-ui/index.html` ✅

## 📊 Cấu trúc project

```
D:\Java\khohoclieu\
├── pom.xml                          (Spring Boot 3.3.5)
├── src/main/
│   ├── java/.../
│   │   ├── controller/              (7 controllers)
│   │   ├── config/
│   │   │   ├── SecurityConfig.java  (Permit Swagger paths)
│   │   │   ├── WebConfig.java       (Resource handlers)
│   │   │   └── SwaggerConfig.java   (OpenAPI config)
│   │   ├── service/
│   │   ├── repository/
│   │   └── ...
│   └── resources/
│       ├── application.yml          (context-path: /api/v1)
│       └── ...
└── target/
    └── Preschool_Data_Warehouse-0.0.1-SNAPSHOT.jar
```

## 🎉 Kết luận

✅ **Swagger UI đã hoạt động hoàn hảo!**
✅ **Tất cả 7 controllers đều hiển thị đầy đủ**
✅ **Có thể test API ngay trong giao diện Swagger**
✅ **Hỗ trợ JWT authentication**

---

**Ngày hoàn thành:** 18/11/2025  
**Status:** ✅ WORKING  
**Spring Boot:** 3.3.5  
**SpringDoc:** 2.6.0

