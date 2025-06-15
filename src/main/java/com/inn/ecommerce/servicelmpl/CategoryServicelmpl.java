package com.inn.ecommerce.servicelmpl;

import com.inn.ecommerce.JWT.JwtUtil;
import com.inn.ecommerce.POJO.Category;
import com.inn.ecommerce.constents.EcommerceConstants;
import com.inn.ecommerce.dao.CategoryDao;
import com.inn.ecommerce.service.CategoryService;
import com.inn.ecommerce.utils.EcommerceUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.*;

@Slf4j
@Service
public class CategoryServicelmpl implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EntityManager entityManager;

    @Override
    public ResponseEntity<String> createCategory(Map<String, String> requestMap) {
        log.info("Inside addCategory {}", requestMap);
        try {
            if (!requestMap.containsKey("name")) {
                return EcommerceUtils.getResponseEntity("Category name is required", HttpStatus.BAD_REQUEST);
            }

            String categoryName = requestMap.get("name");
            if (Objects.nonNull(categoryDao.findByName(categoryName))) {
                return EcommerceUtils.getResponseEntity("Category already exists", HttpStatus.BAD_REQUEST);
            }

            Category category = new Category();
            category.setName(categoryName);
            categoryDao.save(category);

            return EcommerceUtils.getResponseEntity("Category added successfully", HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error in addCategory", ex);
            return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> updateCategory(Integer id, Map<String, String> requestMap) {
        try {
            Optional<Category> optionalCategory = categoryDao.findById(id);
            if (optionalCategory.isEmpty()) {
                return EcommerceUtils.getResponseEntity("Category id doesn't exist", HttpStatus.BAD_REQUEST);
            }

            Category category = optionalCategory.get();
            if (requestMap.containsKey("name")) {
                String newName = requestMap.get("name");
                // Vérifiez si le nouveau nom existe déjà
                if (categoryDao.findByName(newName) != null && !category.getName().equals(newName)) {
                    return EcommerceUtils.getResponseEntity("Category name already exists", HttpStatus.BAD_REQUEST);
                }
                category.setName(newName);
            }

            categoryDao.save(category);
            return EcommerceUtils.getResponseEntity("Category updated successfully", HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error in updateCategory", ex);
            return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> getAllCategories() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_admin"));

            if (!isAdmin) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<Category> categoryList = categoryDao.findAll();
            List<Map<String, Object>> response = new ArrayList<>();

            for (Category category : categoryList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", category.getId());
                map.put("name", category.getName());
                response.add(map);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error in getAllCategories", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> deleteCategory(Integer id) {
        try {
            if (!categoryDao.existsById(id)) {
                return EcommerceUtils.getResponseEntity("Category id doesn't exist", HttpStatus.BAD_REQUEST);
            }

            categoryDao.deleteById(id);
            return EcommerceUtils.getResponseEntity("Category deleted successfully", HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in deleteCategory", ex);
            return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}