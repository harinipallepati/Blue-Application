package com.example.blue.controller;

import com.example.blue.model.Product;
import com.example.blue.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    @Test
    void shouldReturnProductsById() throws Exception {
        Product p1 = new Product();
        p1.setName("Laptop");
        p1.setPrice(50000);
        p1.setStock(10);

        when(service.getProductById(1L))
                .thenReturn(p1);

        mockMvc.perform(get("/products/id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(50000))
                .andExpect(jsonPath("$.stock").value(10));

    }
    @Test
    void shouldDeleteProduct() throws Exception {
        doNothing().when(service).deleteProduct(1L);

        mockMvc.perform(delete("/products/id/1"))
                .andExpect(status().isOk());
        verify(service).deleteProduct(1L);
        }
    @Test
    void shouldAddProduct() throws Exception {
        Product p1=new Product();
        p1.setPrice(50000);
        p1.setName("Laptop");
        p1.setStock(10);

        when(service.addProduct(any(Product.class)))
                .thenReturn(p1);
        ObjectMapper mapper=new ObjectMapper();
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(p1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(50000))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.stock").value(10))
                .andDo(print());
    }
    @Test
    void ShouldUpdateProduct() throws Exception {
        Product p1=new Product();
        p1.setStock(20);
        p1.setName("Phone");
        p1.setPrice(25000);

        when(service.updateProduct(eq(1L),any(Product.class)))
                .thenReturn(p1);
        ObjectMapper mapper=new ObjectMapper();
        mockMvc.perform(put("/products/id/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(p1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(20))
                .andExpect(jsonPath("$.name").value("Phone"))
                .andExpect(jsonPath("$.price").value(25000))
                .andDo(print());
    }
    @Test
    void shouldGetProductByPrecise() throws Exception {
        Product p1=new Product();
        p1.setName("Laptop");
        p1.setPrice(50000);
        p1.setStock(10);

        List<Product> products=List.of(p1);
        Page<Product> page=new PageImpl<>(products);
        when(service.getProductsByPrecise("Laptop",0,5,"desc"))
                .thenReturn(page);
        mockMvc.perform(get("/products")
                .param("name","Laptop")
                .param("page","0")
                .param("size","5")
                .param("sort","desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop"))
                .andExpect(jsonPath("$.content[0].price").value(50000))
                .andExpect(jsonPath("$.content[0].stock").value(10))
                .andDo(print());
    }
}
