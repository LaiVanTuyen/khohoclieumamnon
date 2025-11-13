package com.khohoclieumnhongphong.khohoclieu.config;

import com.khohoclieumnhongphong.khohoclieu.security.CustomAuthenticationEntryPoint;
import com.khohoclieumnhongphong.khohoclieu.security.jwt.JwtAuthFilter;
import com.khohoclieumnhongphong.khohoclieu.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor // Tự động inject (autowired) các dependency final
public class SecurityConfig {

    // Inject các class bạn đã tạo:
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsServiceImpl; // Service của bạn
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint; // Xử lý lỗi 401

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Cung cấp UserDetailsService (của bạn) cho Spring
        authProvider.setUserDetailsService(userDetailsServiceImpl);
        // Cung cấp PasswordEncoder
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF
                .cors(cors -> {}) // Kích hoạt CORS (đã config ở WebConfig)

                // Xử lý lỗi 401 (chưa xác thực)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint))

                // Set session STATELESS (không lưu session)
                .sessionManagement(ss -> ss.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Phân quyền Endpoint
                .authorizeHttpRequests(auth -> auth
                        // --- Endpoints công khai (Public) ---
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/resources/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/topics/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/types/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/banners/**").permitAll()

                        // --- Swagger UI ---
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // --- Endpoints cần quyền (Protected) ---
                        .requestMatchers(HttpMethod.POST, "/api/resources").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/api/resources/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.DELETE, "/api/resources/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Yêu cầu đăng nhập cho tất cả các request khác
                        .anyRequest().authenticated()
                );

        // Đặt AuthenticationProvider (của bạn)
        http.authenticationProvider(authenticationProvider());

        // Thêm filter JWT (của bạn) vào đúng vị trí
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}