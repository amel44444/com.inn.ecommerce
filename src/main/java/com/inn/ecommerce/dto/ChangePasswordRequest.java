package com.inn.ecommerce.dto;

public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;

    // Constructeur vide
    public ChangePasswordRequest() {
    }

    // Getters et Setters
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
