package com.giovani.productapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giovani.productapi.dto.ProductRequest;
import com.giovani.productapi.dto.ProductResponse;
import com.giovani.productapi.exceptions.ResourceNotFoundException;
import com.giovani.productapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateProduct() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("Notebook")
                .description("Dell XPS")
                .price(BigDecimal.valueOf(4500))
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Notebook")
                .description("Dell XPS")
                .price(BigDecimal.valueOf(4500))
                .imageUrl("https://example.com/notebook.png")
                .build();

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/notebook.png"));
    }

    @Test
    void testCreateProduct_InvalidRequest() throws Exception {
        ProductRequest invalidRequest = ProductRequest.builder()
                .name("")
                .price(BigDecimal.ZERO)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("must not be blank"))
                .andExpect(jsonPath("$.errors.price").value("must be greater than 0.0"));
    }

    @Test
    void testGetAllProducts() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder()
                        .id(1L)
                        .name("Produto A")
                        .description("Desc A")
                        .price(BigDecimal.TEN)
                        .imageUrl("img-a.png")
                        .build(),
                ProductResponse.builder()
                        .id(2L)
                        .name("Produto B")
                        .description("Desc B")
                        .price(BigDecimal.ONE)
                        .imageUrl("img-b.png")
                        .build()
        );

        when(service.findAll()).thenReturn(products);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetProductById() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Produto A")
                .description("Desc A")
                .price(BigDecimal.TEN)
                .imageUrl("img-a.png")
                .build();

        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.imageUrl").value("img-a.png"));
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Produto não encontrado"));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Produto não encontrado"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        ProductRequest update = ProductRequest.builder()
                .name("Novo Produto")
                .description("Nova Desc")
                .price(BigDecimal.valueOf(99))
                .build();

        ProductResponse updated = ProductResponse.builder()
                .id(1L)
                .name("Novo Produto")
                .description("Nova Desc")
                .price(BigDecimal.valueOf(99))
                .imageUrl("nova.png")
                .build();

        when(service.update(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novo Produto"))
                .andExpect(jsonPath("$.imageUrl").value("nova.png"));
    }

    @Test
    void testUpdateProduct_NotFound() throws Exception {
        ProductRequest update = ProductRequest.builder()
                .name("Teste")
                .description("teste")
                .price(BigDecimal.TEN)
                .build();

        when(service.update(eq(123L), any())).thenThrow(new ResourceNotFoundException("Produto não encontrado"));

        mockMvc.perform(put("/api/v1/products/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Produto não encontrado"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProduct_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Produto não encontrado"))
                .when(service).delete(99L);

        mockMvc.perform(delete("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Produto não encontrado"));
    }
}