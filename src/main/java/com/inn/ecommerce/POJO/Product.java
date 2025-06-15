package com.inn.ecommerce.POJO;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "TINYINT(1)")
    private Integer id;

    private String name;

    private String description;

    private Integer price;

    private Boolean status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_fk", referencedColumnName = "id")
    private Category category;
}
