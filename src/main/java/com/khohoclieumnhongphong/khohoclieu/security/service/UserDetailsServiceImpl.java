package com.khohoclieumnhongphong.khohoclieu.security.service;

import com.khohoclieumnhongphong.khohoclieu.entity.Role;
import com.khohoclieumnhongphong.khohoclieu.entity.User;
import com.khohoclieumnhongphong.khohoclieu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional // Cần thiết để tải lazy-loaded roles
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Spring Security gọi 'username', nhưng chúng ta dùng 'email'
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + email));

        // Chuyển đổi Set<Role> của chúng ta sang Collection<GrantedAuthority> của Spring
        Collection<? extends GrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

        // Trả về đối tượng UserDetails mà Spring Security cần
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities // Cung cấp quyền cho Spring
        );
    }

    // Helper method để chuyển đổi Set<Role> sang List<GrantedAuthority>
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream()
                // Chuyển Role (vd: ROLE_ADMIN) thành 1 object GrantedAuthority
                .map(role -> new SimpleGrantedAuthority(role.getName().name())) // .name() vì Role của ta là Enum
                .collect(Collectors.toList());
    }
}