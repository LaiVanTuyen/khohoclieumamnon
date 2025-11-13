-- V1__init_schema.sql
-- File này tạo CSDL ban đầu cho dự án Kho Học Liệu Mầm Non

-- -----------------------------------------------------
-- Bảng: roles (Quyền)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `roles` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `name` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
    ENGINE = InnoDB;

-- CHÈN DỮ LIỆU BAN ĐẦU CHO BẢNG `roles`
-- Đây là bước quan trọng để ứng dụng có thể hoạt động ngay lập tức
INSERT INTO `roles` (`name`) VALUES ('ROLE_ADMIN')
    ON DUPLICATE KEY UPDATE name='ROLE_ADMIN';

INSERT INTO `roles` (`name`) VALUES ('ROLE_TEACHER')
    ON DUPLICATE KEY UPDATE name='ROLE_TEACHER';


-- -----------------------------------------------------
-- Bảng: users (Người dùng)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(255) NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Bảng: user_roles (Bảng nối User và Role - ManyToMany)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_roles` (
                                            `user_id` BIGINT NOT NULL,
                                            `role_id` BIGINT NOT NULL,
                                            PRIMARY KEY (`user_id`, `role_id`),
    INDEX `fk_user_roles_role_id_idx` (`role_id` ASC) VISIBLE,
    CONSTRAINT `fk_user_roles_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_user_roles_role_id`
    FOREIGN KEY (`role_id`)
    REFERENCES `roles` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Bảng: topics (Chủ đề)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `topics` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(255) NOT NULL,
    `slug` VARCHAR(255) NOT NULL,
    `description` TEXT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `slug_UNIQUE` (`slug` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Bảng: resource_types (Loại tài liệu)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `resource_types` (
                                                `id` BIGINT NOT NULL AUTO_INCREMENT,
                                                `name` VARCHAR(255) NOT NULL,
    `slug` VARCHAR(255) NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `slug_UNIQUE` (`slug` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Bảng: banners (Quảng cáo/Banner trang chủ)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banners` (
                                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                                         `title` VARCHAR(255) NOT NULL,
    `image_url` VARCHAR(1024) NOT NULL,
    `link_url` VARCHAR(1024) NULL,
    `is_active` TINYINT(1) NOT NULL DEFAULT 1,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Bảng: resources (Tài liệu học tập - Cốt lõi)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `resources` (
                                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                                           `title` VARCHAR(255) NOT NULL,
    `description` TEXT NULL,
    `status` ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    `file_url` VARCHAR(1024) NULL,
    `original_file_name` VARCHAR(255) NULL,
    `file_size` BIGINT NULL,
    `youtube_url` VARCHAR(255) NULL,
    `uploader_id` BIGINT NOT NULL,
    `approver_id` BIGINT NULL,
    `topic_id` BIGINT NOT NULL,
    `type_id` BIGINT NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `approved_at` TIMESTAMP NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_resources_uploader_id_idx` (`uploader_id` ASC) VISIBLE,
    INDEX `fk_resources_approver_id_idx` (`approver_id` ASC) VISIBLE,
    INDEX `fk_resources_topic_id_idx` (`topic_id` ASC) VISIBLE,
    INDEX `fk_resources_type_id_idx` (`type_id` ASC) VISIBLE,
    INDEX `idx_resources_status` (`status` ASC) VISIBLE,
    CONSTRAINT `fk_resources_uploader_id`
    FOREIGN KEY (`uploader_id`)
    REFERENCES `users` (`id`)
    ON DELETE RESTRICT -- hoặc ON DELETE SET NULL
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_resources_approver_id`
    FOREIGN KEY (`approver_id`)
    REFERENCES `users` (`id`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_resources_topic_id`
    FOREIGN KEY (`topic_id`)
    REFERENCES `topics` (`id`)
    ON DELETE RESTRICT
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_resources_type_id`
    FOREIGN KEY (`type_id`)
    REFERENCES `resource_types` (`id`)
    ON DELETE RESTRICT
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;