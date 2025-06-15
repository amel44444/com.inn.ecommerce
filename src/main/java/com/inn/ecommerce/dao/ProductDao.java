package com.inn.ecommerce.dao;

import com.inn.ecommerce.POJO.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductDao extends JpaRepository<Product, Integer> {

    List<Product> findByCategoryId(Integer categoryId);
    Optional<Product> findById(Integer id);


    // tu peux ajouter des méthodes personnalisées si besoin
}
