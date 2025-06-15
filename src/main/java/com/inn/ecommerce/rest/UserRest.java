package com.inn.ecommerce.rest;

import com.inn.ecommerce.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path="/user")
public interface UserRest {
    @PostMapping(path="/signup")
    ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path="/login")
    ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap);

    @GetMapping("/get")
   ResponseEntity<List<UserWrapper>> getAllUsers(@RequestHeader("Authorization") String authHeader);

    @PostMapping("/update")
    ResponseEntity<String> updateUser(@RequestBody Map<String, String> requestMap); // ✅ Ajout de @RequestBody
}



