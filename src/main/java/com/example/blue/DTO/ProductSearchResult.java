package com.example.blue.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductSearchResult {
    private String name;
    private int price;
    private int stock;
    private String description;
}
