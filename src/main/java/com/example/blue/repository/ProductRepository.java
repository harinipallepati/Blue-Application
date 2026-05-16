package com.example.blue.repository;

import com.example.blue.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
