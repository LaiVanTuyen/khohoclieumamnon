-- File V2: Chèn dữ liệu khởi tạo (Seed Data)
-- File này chỉ chạy SAU KHI V1 (tạo bảng) chạy thành công.
-- Chúng ta giả định V1 đã chèn 3 Trạng thái (PENDING, APPROVED, REJECTED)
-- và 2 Quyền (ROLE_ADMIN, ROLE_TEACHER).

SET SESSION default_storage_engine = 'InnoDB';
SET NAMES 'utf8mb4';

-- -----------------------------------------------------
-- CHÈN TÀI KHOẢN ADMIN ĐẦU TIÊN
-- -----------------------------------------------------

-- Mật khẩu là: "admin123" (đã được mã hóa bằng BCrypt)
-- Bạn PHẢI dùng mật khẩu đã mã hóa, vì Spring Security sẽ dùng BCrypt để so sánh
SET @admin_email = 'admin@khohoclieu.com';
SET @admin_password_hash = '$2a$10$f/eC.xS6X8.j3xY.C1qJd.eJ9R.0cO0G/K1lB6/foW0E..F1X98e';

INSERT INTO `users` (`email`, `password`, `full_name`, `created_at`, `updated_at`)
VALUES
    (@admin_email, @admin_password_hash, 'Quản Trị Viên', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON DUPLICATE KEY UPDATE email=@admin_email; -- Đề phòng chạy lại


-- -----------------------------------------------------
-- PHÂN QUYỀN ADMIN CHO TÀI KHOẢN TRÊN
-- -----------------------------------------------------

-- Lấy ID của user 'admin@khohoclieu.com' VÀ ID của 'ROLE_ADMIN'
-- Sau đó chèn vào bảng user_roles
INSERT INTO `user_roles` (`user_id`, `role_id`)
SELECT
    (SELECT `id` FROM `users` WHERE `email` = @admin_email),
    (SELECT `id` FROM `roles` WHERE `name` = 'ROLE_ADMIN')
    ON DUPLICATE KEY UPDATE user_id = user_id; -- Bỏ qua nếu đã tồn tại


-- -----------------------------------------------------
-- CHÈN DỮ LIỆU MẪU CHO TOPICS (CHỦ ĐỀ)
-- -----------------------------------------------------
INSERT INTO `topics` (`name`, `slug`, `description`)
VALUES
    ('Thơ, truyện', 'tho-truyen', 'Các bài thơ, câu truyện kể cho bé'),
    ('Bài hát', 'bai-hat', 'Âm nhạc và các bài hát mầm non'),
    ('Khám phá', 'kham-pha', 'Nội dung khám phá khoa học và xã hội'),
    ('Hoạt động tạo hình', 'hoat-dong-tao-hinh', 'Nặn, vẽ, xé dán...')
    ON DUPLICATE KEY UPDATE name = VALUES(name);


-- -----------------------------------------------------
-- CHÈN DỮ LIỆU MẪU CHO RESOURCE_TYPES (LOẠI TÀI LIỆU)
-- -----------------------------------------------------
INSERT INTO `resource_types` (`name`, `slug`)
VALUES
    ('Video', 'video'),
    ('Hình ảnh', 'hinh-anh'),
    ('Tài liệu (PDF)', 'tai-lieu-pdf'),
    ('Bài giảng (PPT)', 'bai-giang-ppt')
    ON DUPLICATE KEY UPDATE name = VALUES(name);