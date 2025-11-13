package com.khohoclieumnhongphong.khohoclieu.security.jwt;

import com.khohoclieumnhongphong.khohoclieu.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    // [THÊM] Định nghĩa logger SLF4J của riêng bạn
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Lấy token từ header
            String jwt = getJwtFromRequest(request);

            // 2. Nếu token hợp lệ
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

                // 3. Lấy email từ token
                String email = jwtTokenProvider.getEmailFromToken(jwt);

                // 4. Tải thông tin User (gồm cả quyền) từ DB
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 5. Tạo đối tượng Authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Không cần password
                        userDetails.getAuthorities() // Quyền
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. [QUAN TRỌNG] Set user vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {

            // [SỬA] Ghi log theo cách chuẩn
            // Cách này sẽ ghi đầy đủ cả message và stack trace, tốt hơn cho việc debug
            logger.error("Không thể set user authentication", ex);
        }

        // 7. Cho request đi tiếp
        filterChain.doFilter(request, response);
    }

    // Helper: Lấy token từ "Authorization: Bearer [token]"
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Bỏ "Bearer "
        }
        return null;
    }
}