package com.example.blue.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    @Min(value = 0)
    private int price;
    @Min(value = 0)
    private int stock;
    private String description;

    public Product() {}

    public Product(String name,int price, int stock){
        this.name=name;
        this.price=price;
        this.stock=stock;
    }
}
