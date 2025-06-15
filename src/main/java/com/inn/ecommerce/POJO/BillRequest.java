package com.inn.ecommerce.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BillRequest {

    private String uuid;
    private String email;
    private String contactNumber;
    private String paymentMethod;
    private Double total;
    private String createdBy;

    // Liste des produits simplifiés dans la facture
    @JsonProperty("products")
    private List<ProductInBill> products;

    // Getters et setters

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<ProductInBill> getProducts() {
        return products;
    }

    public void setProducts(List<ProductInBill> products) {
        this.products = products;
    }
}
