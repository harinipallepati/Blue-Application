package com.example.blue.controller;
import com.example.blue.model.Product;
import com.example.blue.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import javax.naming.Name;
import java.util.List;

@RestController

@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService service;

    @PostMapping
    public Product addProduct(@Valid @RequestBody Product product){
        return service.addProduct(product);
    }
    @GetMapping("id/{id}")
    public Product getProductById(@PathVariable Long id){
        return service.getProductById(id);
    }
    @PutMapping("id/{id}")
    public Product updateProduct(@PathVariable Long id,@Valid @RequestBody Product Product){
        return service.updateProduct(id,Product);
    }
    @DeleteMapping("id/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        service.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping
    public Page<Product>getProductsByPrecise(@RequestParam(required = false)String name,@RequestParam(defaultValue = "0")int page,@RequestParam(defaultValue = "5") int size,@RequestParam(defaultValue = "id")String sort){
        return service.getProductsByPrecise(name,page,size,sort);
    }
}
