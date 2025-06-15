package com.inn.ecommerce.servicelmpl;

import com.inn.ecommerce.JWT.JwtUtil;
import com.inn.ecommerce.POJO.Category;
import com.inn.ecommerce.POJO.Product;
import com.inn.ecommerce.constents.EcommerceConstants;
import com.inn.ecommerce.dao.CategoryDao;
import com.inn.ecommerce.dao.ProductDao;
import com.inn.ecommerce.service.ProductService;
import com.inn.ecommerce.utils.EcommerceUtils;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ProductServicelmpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EntityManager entityManager;

    @Override
    public ResponseEntity<String> createProduct(Map<String, String> requestMap) {
        log.info("Inside createProduct {}", requestMap);
        try {
            if (!requestMap.containsKey("name") || !requestMap.containsKey("price") || !requestMap.containsKey("categoryId")) {
                return EcommerceUtils.getResponseEntity("Name, price and categoryId are required", HttpStatus.BAD_REQUEST);
            }

            String name = requestMap.get("name");
            Integer price;
            Integer categoryId;
            try {
                price = Integer.parseInt(requestMap.get("price"));
                categoryId = Integer.parseInt(requestMap.get("categoryId"));
            } catch (NumberFormatException e) {
                return EcommerceUtils.getResponseEntity("Price and categoryId must be valid integers", HttpStatus.BAD_REQUEST);
            }

            Optional<Category> categoryOpt = categoryDao.findById(categoryId);
            if (categoryOpt.isEmpty()) {
                return EcommerceUtils.getResponseEntity("Category not found", HttpStatus.BAD_REQUEST);
            }

            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setCategory(categoryOpt.get());
            product.setDescription(requestMap.getOrDefault("description", ""));
            product.setStatus(Boolean.parseBoolean(requestMap.getOrDefault("status", "true")));

            productDao.save(product);

            return EcommerceUtils.getResponseEntity("Product added successfully", HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error in createProduct", ex);
            return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> updateProduct(Integer id, Map<String, String> requestMap) {
        try {
            Optional<Product> productOpt = productDao.findById(id);
            if (productOpt.isEmpty()) {
                return EcommerceUtils.getResponseEntity("Product id doesn't exist", HttpStatus.BAD_REQUEST);
            }

            Product product = productOpt.get();

            if (requestMap.containsKey("name")) {
                product.setName(requestMap.get("name"));
            }
            if (requestMap.containsKey("price")) {
                try {
                    product.setPrice(Integer.parseInt(requestMap.get("price")));
                } catch (NumberFormatException e) {
                    return EcommerceUtils.getResponseEntity("Price must be a valid integer", HttpStatus.BAD_REQUEST);
                }
            }
            if (requestMap.containsKey("description")) {
                product.setDescription(requestMap.get("description"));
            }
            if (requestMap.containsKey("status")) {
                product.setStatus(Boolean.parseBoolean(requestMap.get("status")));
            }
            if (requestMap.containsKey("categoryId")) {
                try {
                    Integer categoryId = Integer.parseInt(requestMap.get("categoryId"));
                    Optional<Category> categoryOpt = categoryDao.findById(categoryId);
                    if (categoryOpt.isEmpty()) {
                        return EcommerceUtils.getResponseEntity("Category not found", HttpStatus.BAD_REQUEST);
                    }
                    product.setCategory(categoryOpt.get());
                } catch (NumberFormatException e) {
                    return EcommerceUtils.getResponseEntity("categoryId must be a valid integer", HttpStatus.BAD_REQUEST);
                }
            }

            productDao.save(product);
            return EcommerceUtils.getResponseEntity("Product updated successfully", HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error in updateProduct", ex);
            return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        try {
            // Exemple de vérification de rôle admin, comme dans CategoryServiceImpl
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_admin"));

            if (!isAdmin) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<Product> productList = productDao.findAll();
            List<Map<String, Object>> response = new ArrayList<>();

            for (Product product : productList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", product.getId());
                map.put("name", product.getName());
                map.put("description", product.getDescription());
                map.put("price", product.getPrice());
                map.put("status", product.getStatus());
                if (product.getCategory() != null) {
                    map.put("categoryId", product.getCategory().getId());
                    map.put("categoryName", product.getCategory().getName());
                } else {
                    map.put("categoryId", null);
                    map.put("categoryName", null);
                }
                response.add(map);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error in getAllProducts", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            if (!productDao.existsById(id)) {
                return EcommerceUtils.getResponseEntity("Product id doesn't exist", HttpStatus.BAD_REQUEST);
            }

            productDao.deleteById(id);
            return EcommerceUtils.getResponseEntity("Product deleted successfully", HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error in deleteProduct", ex);
            return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
