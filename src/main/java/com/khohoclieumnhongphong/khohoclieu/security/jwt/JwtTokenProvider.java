package com.khohoclieumnhongphong.khohoclieu.security.jwt;

import com.khohoclieumnhongphong.khohoclieu.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final Key jwtSecretKey;
    private final long jwtExpirationMs;

    // Đọc secret và expiration từ file config (thay vì @Value)
    @Autowired
    public JwtTokenProvider(AppProperties appProperties) {
        String jwtSecret = appProperties.getJwt().getSecret();
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationMs = appProperties.getJwt().getExpirationMs();
    }

    // Tạo token từ đối tượng Authentication (sau khi login thành công)
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromEmail(userPrincipal.getUsername());
    }

    // (Helper) Tạo token từ email
    public String generateTokenFromEmail(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(email) // Gán email làm "chủ" của token
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS512) // Ký token
                .compact();
    }

    // Lấy email (subject) từ token
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Kiểm tra xem token có hợp lệ không (không hết hạn, đúng chữ ký)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT không hợp lệ: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT đã hết hạn: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT không được hỗ trợ: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("Chuỗi claim JWT trống: {}", ex.getMessage());
        }
        return false;
    }
}