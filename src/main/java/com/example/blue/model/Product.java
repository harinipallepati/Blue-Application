package com.example.blue.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;


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

    public Product() {}

    public Product(String name,int price, int stock){
        this.name=name;
        this.price=price;
        this.stock=stock;
    }

    public Long getId(){return id;}
    public String getName(){return name;}
    public int getPrice(){return price;}
    public int getStock(){return stock;}

    public void setName(String name){this.name=name;}
    public void setPrice(int price){this.price=price;}
    public void setStock(int stock){this.stock=stock;}

}
