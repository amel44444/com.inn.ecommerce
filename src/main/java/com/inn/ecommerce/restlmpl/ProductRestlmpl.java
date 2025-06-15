package com.inn.ecommerce.restlmpl;

import com.inn.ecommerce.rest.ProductRest;
import com.inn.ecommerce.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductRestlmpl implements ProductRest {

    @Autowired
    private ProductService productService;

    @Override
    @PostMapping("/create")
    public ResponseEntity<String> createProduct(@RequestBody Map<String, String> requestMap) {
        try {
            return productService.createProduct(requestMap);
        } catch (Exception ex) {
            log.error("Erreur lors de l'ajout de produit", ex);
            return ResponseEntity.internalServerError().body("Erreur serveur");
        }
    }

    @Override
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Integer id,
                                                @RequestBody Map<String, String> requestMap) {
        try {
            return productService.updateProduct(id, requestMap);
        } catch (Exception ex) {
            log.error("Erreur mise à jour produit", ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        try {
            return productService.deleteProduct(id);
        } catch (Exception ex) {
            log.error("Erreur lors de la suppression de produit", ex);
            return ResponseEntity.internalServerError().body("Erreur serveur");
        }
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        try {
            return productService.getAllProducts();
        } catch (Exception ex) {
            log.error("Erreur lors de la récupération des produits", ex);
            return ResponseEntity.internalServerError().build();
        }
    }
}
