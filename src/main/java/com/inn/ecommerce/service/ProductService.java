package com.inn.ecommerce.service;

import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;

public interface ProductService {
    ResponseEntity<String> createProduct(Map<String, String> requestMap);
    ResponseEntity<String> updateProduct(Integer id, Map<String, String> requestMap);
    ResponseEntity<List<Map<String, Object>>> getAllProducts();
    ResponseEntity<String> deleteProduct(Integer id);
}
