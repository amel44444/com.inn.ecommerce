package com.inn.ecommerce.JWT;

import com.inn.ecommerce.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CustomerUsersDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    private com.inn.ecommerce.POJO.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername: {}", username);
        userDetail = userDao.findByEmail(username);

        if (!Objects.isNull(userDetail)) {
            // IMPORTANT : Spring Security attend "ROLE_" comme préfixe
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userDetail.getRole());
            return new User(userDetail.getEmail(), userDetail.getPassword(), List.of(authority));
        } else {
            throw new UsernameNotFoundException("User not found.");
        }
    }
}
