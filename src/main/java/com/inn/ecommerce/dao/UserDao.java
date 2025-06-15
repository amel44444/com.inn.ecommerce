package com.inn.ecommerce.dao;

import com.inn.ecommerce.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDao extends JpaRepository<User,Integer> {

    User findByEmail(@Param("email")String email);
    List<User> findAll();  // Méthode pour récupérer tous les utilisateurs
}
