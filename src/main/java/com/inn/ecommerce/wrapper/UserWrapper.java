package com.inn.ecommerce.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWrapper {
    private Integer id;               // Identifiant de l'utilisateur
    private String name;              // Nom de l'utilisateur
    private String email;             // Email de l'utilisateur
    private String contactNumber;     // Numéro de contact de l'utilisateur
    private String status;            // Statut de l'utilisateur
    private String role;              // Rôle de l'utilisateur

    // Un seul constructeur avec tous les paramètres nécessaires

}
