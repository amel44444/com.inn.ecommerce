package com.inn.ecommerce.restlmpl;

import com.inn.ecommerce.rest.CategoryRest;
import com.inn.ecommerce.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryRestlmpl implements CategoryRest {

    @Autowired
    private CategoryService categoryService;

    @Override
    @PostMapping("/create")
    public ResponseEntity<String> createCategory(@RequestBody Map<String, String> requestMap) {
        try {
            return categoryService.createCategory(requestMap);
        } catch (Exception ex) {
            log.error("Erreur lors de l'ajout de catégorie", ex);
            return ResponseEntity.internalServerError().body("Erreur serveur");
        }
    }

    @Override
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Integer id,
                                                 @RequestBody Map<String, String> requestMap) {
        try {
            return categoryService.updateCategory(id, requestMap);
        } catch (Exception ex) {
            log.error("Erreur mise à jour catégorie", ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id) {
        try {
            return categoryService.deleteCategory(id);
        } catch (Exception ex) {
            log.error("Erreur lors de la suppression", ex);
            return ResponseEntity.internalServerError().body("Erreur serveur");
        }
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<List<Map<String, Object>>> getAllCategories() {
        try {
            return categoryService.getAllCategories();
        } catch (Exception ex) {
            log.error("Erreur lors de la récupération", ex);
            return ResponseEntity.internalServerError().build();
        }
    }
}