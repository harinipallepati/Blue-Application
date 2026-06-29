package com.example.blue.integration;

import com.example.blue.model.Product;
import com.example.blue.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void testAddProduct() throws Exception {
        Product p1=new Product();
        p1.setName("Mac Book");
        p1.setPrice(82000);
        p1.setStock(10);
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mac Book"))
                .andExpect(jsonPath("$.price").value(82000))
                .andExpect(jsonPath("$.stock").value(10));
    }
    @Test
    void testGetProductById() throws Exception {
        Product p1=new Product();
        p1.setName("Mac Book");
        p1.setPrice(82000);
        p1.setStock(10);
        Product saved=repository.save(p1);
        mockMvc.perform(get("/products/id/"+saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Mac Book"))
                .andExpect(jsonPath("$.price").value(82000))
                .andExpect(jsonPath("$.stock").value(10));
    }
    @Test
    void testGetProductByIdNotFound() throws Exception {
        mockMvc.perform(get("/products/id/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found with id:1"));
    }
    @Test
    void testDeleteProduct() throws Exception {
        Product p1=new Product();
        p1.setName("Mac Book");
        p1.setPrice(82000);
        p1.setStock(10);
        Product saved=repository.save(p1);
        mockMvc.perform(delete("/products/id/"+saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));
        assertFalse(repository.findById(saved.getId()).isPresent());
    }
    @Test
    void testDeleteProductIdNotFound() throws Exception {
        mockMvc.perform(delete("/products/id/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found with id:1"));
    }
    @Test
    void testUpdateProduct() throws Exception {
        Product p1=new Product();
        p1.setName("Mac Book");
        p1.setPrice(82000);
        p1.setStock(10);

        Product p2 =new Product();
        p2.setName("Mac Book 2");
        p2.setPrice(80000);
        p2.setStock(12);
        Product saved=repository.save(p1);
        mockMvc.perform(put("/products/id/"+saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Mac Book 2"))
                .andExpect(jsonPath("$.price").value(80000))
                .andExpect(jsonPath("$.stock").value(12));
    }
    @Test
    void testUpdateProductIdNotFound() throws Exception {
        Product p2 =new Product();
        p2.setName("Mac Book 2");
        p2.setPrice(80000);
        p2.setStock(12);
        mockMvc.perform(put("/products/id/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p2)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found with id:1"));
    }
    @Test
    void testGetProductByPrecise() throws Exception {
        Product p1=new Product();
        p1.setName("Mac Book");
        p1.setPrice(82000);
        p1.setStock(10);
        repository.save(p1);
        mockMvc.perform(get("/products?name=Mac&page=0&size=5&sort=price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Mac Book"))
                .andExpect(jsonPath("$.content[0].price").value(82000))
                .andExpect(jsonPath("$.content[0].stock").value(10));
    }
    @Test
    void testGetProductByPreciseNoName() throws Exception {
        Product p1=new Product();
        p1.setName("Mac Book");
        p1.setPrice(82000);
        p1.setStock(10);
        repository.save(p1);
        mockMvc.perform(get("/products?name=&page=0&size=5&sort=price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Mac Book"))
                .andExpect(jsonPath("$.content[0].price").value(82000))
                .andExpect(jsonPath("$.content[0].stock").value(10));
    }
}
