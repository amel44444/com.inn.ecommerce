package com.inn.ecommerce.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/category")
public interface CategoryRest {

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> createCategory(@RequestBody Map<String, String> requestMap);

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    /*ResponseEntity<String> updateCategory(@RequestBody Map<String, String> requestMap);*/
    ResponseEntity<String> updateCategory(@PathVariable Integer id,
                                          @RequestBody Map<String, String> requestMap);

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> deleteCategory(@PathVariable Integer id);

    @GetMapping("/get")
    ResponseEntity<List<Map<String, Object>>> getAllCategories();
}