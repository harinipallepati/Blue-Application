package com.example.blue.service;

import com.example.blue.exception.ResourceNotFoundException;
import com.example.blue.model.Product;
import com.example.blue.repository.ProductRepository;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.List;
@Service

public class ProductService {
    @Autowired
    private ProductRepository repository;
    public Product addProduct(Product Product){
        return repository.save(Product);
    }
    public Product getProductById(Long id){
        return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product not found with id:"+ id));
    }
    public Product updateProduct(Long id,Product updatedProduct){
        Product existing=repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id:"+ id) );
        existing.setName(updatedProduct.getName());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStock(updatedProduct.getStock());

        return repository.save(existing);
    }
    public void deleteProduct(Long id){
        Product Product=repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product not found with id:"+id));
        repository.delete(Product);
    }
    public Page<Product>getProductsByPrecise(String name,int page,int size,String sort){
        Sort sorting;
        if(sort.contains(",")){
            String[] parts=sort.split(",");
            sorting=Sort.by(Sort.Direction.fromString(parts[1]),parts[0]);
        }
        else{
            sorting=Sort.by(sort);
        }

        PageRequest pageRequest=PageRequest.of(page,size,sorting);
        if(name!=null && !name.isEmpty()){
            return repository.findByNameContainingIgnoreCase(name,pageRequest);
        }
        else{
            return repository.findAll(pageRequest);
        }
    }
}
