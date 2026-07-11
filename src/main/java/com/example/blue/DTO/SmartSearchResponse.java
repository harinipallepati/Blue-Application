package com.example.blue.DTO;

import com.example.blue.model.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SmartSearchResponse {
    private List<ProductSearchResult> products;
}
