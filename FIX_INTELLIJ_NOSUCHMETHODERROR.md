# 🔧 SỬA LỖI NoSuchMethodError KHI CHẠY TỪ INTELLIJ IDEA

## ❌ Vấn đề
Khi chạy từ IntelliJ IDEA, vẫn gặp lỗi:
```
java.lang.NoSuchMethodError: 'void org.springframework.web.method.ControllerAdviceBean.<init>(java.lang.Object)'
```

## 🎯 Nguyên nhân
IntelliJ IDEA đang **cache dependencies cũ** (Spring Boot 3.5.7) mặc dù `pom.xml` đã sửa thành 3.3.5.

## ✅ GIẢI PHÁP - Làm theo thứ tự

### Bước 1: Dừng tất cả process đang chạy
Trong IntelliJ:
1. Click nút **Stop** (⏹️) để dừng app
2. Hoặc ấn **Ctrl+F2** → **Stop All**

### Bước 2: Invalidate Caches và Restart
1. Trong IntelliJ, menu **File** → **Invalidate Caches...**
2. Chọn tất cả options:
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS Log caches and indexes
   - ✅ Clear build/run caches
3. Click **Invalidate and Restart**
4. Đợi IntelliJ restart (30-60 giây)

### Bước 3: Reload Maven Project
Sau khi IntelliJ restart:
1. Mở panel **Maven** (góc phải, hoặc **View** → **Tool Windows** → **Maven**)
2. Click nút **🔄 Reload All Maven Projects** (icon reload ở góc trên bên trái)
3. Đợi Maven download dependencies mới (1-2 phút)

### Bước 4: Clean và Rebuild
Trong Maven panel:
1. Expand **khohoclieu** → **Lifecycle**
2. Double-click **clean**
3. Đợi hoàn thành
4. Double-click **install** hoặc **package**
5. Đợi BUILD SUCCESS

### Bước 5: Rebuild Project
1. Menu **Build** → **Rebuild Project**
2. Đợi rebuild hoàn thành

### Bước 6: Chạy lại application
1. Tìm file `KhohoclieuApplication.java`
2. Right-click → **Run 'KhohoclieuApplication'**
3. Hoặc ấn **Shift+F10**

### Bước 7: Verify Swagger
Sau khi app chạy, mở trình duyệt:
```
http://localhost:8080/api/v1/swagger-ui/index.html
```

## 🚀 CÁCH NHANH - Dùng Terminal trong IntelliJ

Nếu bạn muốn nhanh hơn, dùng Terminal trong IntelliJ:

### Mở Terminal
- Menu **View** → **Tool Windows** → **Terminal**
- Hoặc ấn **Alt+F12**

### Chạy lệnh
```powershell
# Bước 1: Clean build
.\mvnw.cmd clean install -DskipTests

# Bước 2: Chạy app
.\mvnw.cmd spring-boot:run
```

Sau đó mở Swagger UI:
```
http://localhost:8080/api/v1/swagger-ui/index.html
```

## 🔍 KIỂM TRA DEPENDENCIES TRONG INTELLIJ

### Cách 1: Qua Maven Panel
1. Mở **Maven** panel (bên phải)
2. Expand **khohoclieu** → **Dependencies**
3. Tìm **spring-boot-starter-parent**
4. Verify version là **3.3.5** (không phải 3.5.7)

### Cách 2: Qua External Libraries
1. Mở **Project** panel (bên trái)
2. Expand **External Libraries**
3. Tìm **Maven: org.springframework.boot:spring-boot:3.3.5**
4. Nếu thấy version khác 3.3.5 → cần Invalidate Caches

## ⚙️ CẤU HÌNH INTELLIJ (Tùy chọn)

### Đảm bảo IntelliJ dùng đúng JDK
1. Menu **File** → **Project Structure** (Ctrl+Alt+Shift+S)
2. Tab **Project**
3. **SDK:** chọn **Java 17** (hoặc JDK 17)
4. **Language level:** chọn **17**
5. Click **OK**

### Đảm bảo dùng đúng Maven
1. Menu **File** → **Settings** (Ctrl+Alt+S)
2. Tìm **Build, Execution, Deployment** → **Build Tools** → **Maven**
3. **Maven home path:** nên là **Bundled (Maven 3.x)** hoặc path tới Maven của project
4. Click **OK**

## 🐛 NẾU VẪN LỖI

### Xóa thư mục .idea và target
Đóng IntelliJ, rồi chạy PowerShell:
```powershell
cd D:\Java\khohoclieu

# Xóa thư mục IntelliJ cache
Remove-Item -Path .idea -Recurse -Force

# Xóa thư mục build output
Remove-Item -Path target -Recurse -Force

# Xóa IntelliJ module files
Remove-Item -Path *.iml -Force
```

Sau đó:
1. Mở lại IntelliJ
2. Menu **File** → **Open**
3. Chọn thư mục `D:\Java\khohoclieu`
4. IntelliJ sẽ import lại project từ `pom.xml`
5. Đợi Maven download dependencies
6. Chạy lại app

## 📝 CHECKLIST NHANH

Làm theo thứ tự:
- [ ] Dừng app trong IntelliJ
- [ ] **File** → **Invalidate Caches...** → **Invalidate and Restart**
- [ ] Sau khi restart, **Maven panel** → **🔄 Reload All Maven Projects**
- [ ] Maven panel → **Lifecycle** → double-click **clean**
- [ ] Maven panel → **Lifecycle** → double-click **install**
- [ ] **Build** → **Rebuild Project**
- [ ] Run `KhohoclieuApplication` (Shift+F10)
- [ ] Mở http://localhost:8080/api/v1/swagger-ui/index.html

## ✅ KẾT QUẢ MONG ĐỢI

Sau khi làm theo các bước trên:
- ✅ IntelliJ sẽ dùng Spring Boot 3.3.5
- ✅ Lỗi NoSuchMethodError sẽ biến mất
- ✅ App khởi động thành công
- ✅ Swagger UI hoạt động tại `/api/v1/swagger-ui/index.html`
- ✅ API docs hoạt động tại `/api/v1/v3/api-docs`

## 🎓 TẠI SAO CẦN INVALIDATE CACHES?

IntelliJ IDEA cache rất nhiều thứ để tăng tốc:
- Compiled classes
- Maven dependencies
- Indexes
- VCS history

Khi thay đổi phiên bản dependencies lớn (3.5.7 → 3.3.5), cache cũ có thể gây xung đột. **Invalidate Caches** sẽ xóa tất cả cache và reload từ đầu.

---

**Lưu ý:** Nếu sau khi làm tất cả các bước trên vẫn lỗi, hãy:
1. Xóa thư mục `~/.m2/repository/org/springframework` (Maven local cache)
2. Reload Maven project lại
3. IntelliJ sẽ download lại tất cả Spring dependencies

