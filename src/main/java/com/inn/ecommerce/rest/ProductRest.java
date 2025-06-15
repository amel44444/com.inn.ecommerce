package com.inn.ecommerce.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/product")
public interface ProductRest {

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> createProduct(@RequestBody Map<String, String> requestMap);

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> updateProduct(@PathVariable Integer id,
                                         @RequestBody Map<String, String> requestMap);

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> deleteProduct(@PathVariable Integer id);

    @GetMapping("/get")
    ResponseEntity<List<Map<String, Object>>> getAllProducts();
}
