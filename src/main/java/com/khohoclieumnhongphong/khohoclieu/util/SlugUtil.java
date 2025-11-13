package com.khohoclieumnhongphong.khohoclieu.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Tiện ích chuyển đổi chuỗi (ví dụ: "Bài hát mới") thành slug (ví dụ: "bai-hat-moi").
 */
public class SlugUtil {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input) {
        if (input == null) {
            return "";
        }

        // 1. Chuẩn hóa chuỗi (loại bỏ dấu tiếng Việt)
        String noVietnameseSigns = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String normalized = pattern.matcher(noVietnameseSigns)
                .replaceAll("")
                .toLowerCase(Locale.ENGLISH)
                .replace('đ', 'd'); // Xử lý chữ 'đ'

        // 2. Thay thế khoảng trắng bằng dấu gạch ngang
        String noWhitespace = WHITESPACE.matcher(normalized).replaceAll("-");

        // 3. Loại bỏ các ký tự không phải chữ/số/dấu gạch ngang
        String slug = NONLATIN.matcher(noWhitespace).replaceAll("");

        // 4. Loại bỏ các dấu gạch ngang thừa (ví dụ: "---" -> "-")
        return slug.replaceAll("-{2,}", "-").replaceAll("^-|-$", ""); // Bỏ gạch ngang ở đầu/cuối
    }
}