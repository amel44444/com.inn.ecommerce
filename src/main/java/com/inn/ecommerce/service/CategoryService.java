package com.inn.ecommerce.service;

import com.inn.ecommerce.POJO.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    ResponseEntity<String> createCategory(Map<String, String> requestMap);
    ResponseEntity<String> updateCategory(Integer id, Map<String, String> requestMap); // Ajoutez le paramètre id
    ResponseEntity<List<Map<String, Object>>> getAllCategories();
    ResponseEntity<String> deleteCategory(Integer id);



}
