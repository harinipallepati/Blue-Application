package com.example.blue.service;

import com.example.blue.exception.ResourceNotFoundException;
import com.example.blue.model.Product;
import com.example.blue.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository repository;
    @InjectMocks
    private ProductService service;

    @Test
    void testGetProductById() {
        Product p1=new Product();
        p1.setName("Mac Book");
        p1.setPrice(82000);
        p1.setStock(10);

        when(repository.findById(1L))
                .thenReturn(Optional.of(p1));

        Product result=service.getProductById(1L);

        assertEquals("Mac Book",result.getName());
        assertEquals(82000,result.getPrice());
        assertEquals(10,result.getStock());
    }
    @Test
    void testGetProductByIdNotFound() {
        when(repository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->service.getProductById(1L));
    }
    @Test
    void testAddProduct() {
        Product p1=new Product();
        p1.setName("Mac Book");
        p1.setPrice(82000);
        p1.setStock(10);

        when(repository.save(p1))
                .thenReturn(p1);
        Product result=service.addProduct(p1);

        assertEquals("Mac Book",result.getName());
        assertEquals(82000,result.getPrice());
        assertEquals(10,result.getStock());
    }
    @Test
    void testDeleteProduct() {
        Product p1=new Product();
        p1.setName("Mac Book");
        p1.setPrice(82000);
        p1.setStock(10);

        when(repository.findById(1L))
                .thenReturn(Optional.of(p1));
        service.deleteProduct(1L);
        verify(repository).delete(p1);
    }
    @Test
    void testDeleteProductNotFound() {
        when(repository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->service.deleteProduct(1L));
    }
    @Test
    void testUpdateProduct() {
        Product p1=new Product();
        p1.setName("Mac Book");
        p1.setPrice(82000);
        p1.setStock(10);

        Product p2=new Product();
        p2.setName("Mac Book 2");
        p2.setPrice(86000);
        p2.setStock(10);

        when(repository.findById(1L))
                .thenReturn(Optional.of(p1));
        when(repository.save(p1))
                .thenReturn(p1);

        Product result=service.updateProduct(1L,p2);
        assertEquals("Mac Book 2",result.getName());
        assertEquals(86000,result.getPrice());
        assertEquals(10,result.getStock());
    }
    @Test
    void testUpdateProductNotFound() {
        Product p1=new Product();
        p1.setPrice(82000);
        p1.setStock(10);
        p1.setName("Mac Book");

        when(repository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->service.updateProduct(1L,p1));
    }
    @Test
    void testGetProductByPrecise() {
        Product p1=new Product();
        p1.setStock(10);
        p1.setPrice(82000);
        p1.setName("Mac Book");
        Page <Product> page=new PageImpl<>(List.of(p1));
        when(repository.findByNameContainingIgnoreCase(eq("Mac Book"),
                any(Pageable.class)))
                .thenReturn(page);
        Page <Product> result=service.getProductsByPrecise("Mac Book",0,5,"price,desc");
        assertEquals(1,result.getContent().size());

        verify(repository).findByNameContainingIgnoreCase(eq("Mac Book"),any(Pageable.class));
    }
    @Test
    void testGetProductByPreciseNoName() {
        Product p1=new Product();
        p1.setStock(10);
        p1.setPrice(82000);
        p1.setName("Mac Book");
        Page <Product> page=new PageImpl<>(List.of(p1));
        when(repository.findAll(any(Pageable.class)))
                .thenReturn(page);
        Page <Product> result=service.getProductsByPrecise("",0,5,"price,desc");
        assertEquals(1,result.getContent().size());

        verify(repository).findAll(any(Pageable.class));

    }
}
