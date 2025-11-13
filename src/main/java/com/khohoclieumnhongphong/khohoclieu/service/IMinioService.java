package com.khohoclieumnhongphong.khohoclieu.service;

import org.springframework.web.multipart.MultipartFile;

public interface IMinioService {

    /**
     * Tải file lên MinIO.
     *
     * @param file      File (ảnh, video...) từ request.
     * @param subFolder Tên thư mục con trong bucket (ví dụ: "resources", "banners").
     * @return Tên đối tượng (object name) đầy đủ của file đã lưu (ví dụ: "resources/uuid-filename.mp4").
     */
    String uploadFile(MultipartFile file, String subFolder);

    /**
     * Xóa file khỏi MinIO.
     *
     * @param objectName Tên đối tượng (path) đầy đủ của file cần xóa.
     */
    void deleteFile(String objectName);

    /**
     * Lấy URL công khai (public URL) đầy đủ của một file.
     * (Giả định bucket của bạn được cấu hình public-read).
     *
     * @param objectName Tên đối tượng (path) của file.
     * @return URL đầy đủ để truy cập file (ví dụ: http://minio-url:9000/bucket-name/object-name).
     */
    String getFileUrl(String objectName);
}