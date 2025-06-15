package com.inn.ecommerce.service;

import com.inn.ecommerce.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<String> signUp(Map<String, String> requestMap);
    ResponseEntity<String> login(Map<String, String> requestMap); // 👈 Cette méthode est attendue
    //ResponseEntity<List<UserWrapper>> getAllUsers();
    ResponseEntity<String> updateUser(Map<String, String> requestMap);


    ResponseEntity<List<UserWrapper>> getAllUsers(String authHeader);

    ResponseEntity<String> updatePassword(Map<String, String> requestMap, String authHeader);


    ResponseEntity<String> forgotPassword(Map<String, String> requestMap);

}

