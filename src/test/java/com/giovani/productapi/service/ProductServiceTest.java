package com.giovani.productapi.service;

import com.giovani.productapi.dto.ProductRequest;
import com.giovani.productapi.dto.ProductResponse;
import com.giovani.productapi.entity.Product;
import com.giovani.productapi.exceptions.ResourceNotFoundException;
import com.giovani.productapi.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository repository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createProduct_ShouldReturnProductResponse() {
        ProductRequest request = ProductRequest.builder()
                .name("Notebook DELL")
                .description("Dell XPS 13")
                .price(BigDecimal.valueOf(4500))
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Notebook DELL")
                .description("Dell XPS 13")
                .price(BigDecimal.valueOf(4500))
                .build();

        when(repository.save(any())).thenReturn(savedProduct);

        ProductResponse response = productService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedProduct.getId());
        assertThat(response.getName()).isEqualTo(savedProduct.getName());
        assertThat(response.getDescription()).isEqualTo(savedProduct.getDescription());
        assertThat(response.getPrice()).isEqualTo(savedProduct.getPrice());
    }

    @Test
    void findAllProducts_ShouldReturnListOfProducts() {
        List<Product> products = List.of(
                new Product(1L, "Produto 1", "Desc", BigDecimal.TEN, null),
                new Product(2L, "Produto 2", "Desc", BigDecimal.ONE, null)
        );

        when(repository.findAll()).thenReturn(products);

        List<ProductResponse> response = productService.findAll();

        assertThat(response).isNotNull();
        assertThat(response).hasSize(2);
    }

    @Test
    void findProductById_ShouldReturnProductResponse() {
        Long productId = 1L;
        Product product = new Product(productId, "Produto 1", "Desc", BigDecimal.TEN, null);

        when(repository.findById(productId)).thenReturn(java.util.Optional.of(product));

        ProductResponse response = productService.findById(productId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(product.getId());
        assertThat(response.getName()).isEqualTo(product.getName());
        assertThat(response.getDescription()).isEqualTo(product.getDescription());
        assertThat(response.getPrice()).isEqualTo(product.getPrice());
    }

    @Test
    void findById_WhenProductNotExists_ShouldThrowException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Produto não encontrado");
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProductResponse() {
        Long productId = 1L;
        ProductRequest request = ProductRequest.builder()
                .name("Produto Atualizado")
                .description("Descrição Atualizada")
                .price(BigDecimal.valueOf(2000))
                .build();

        Product existingProduct = new Product(productId, "Produto Antigo", "Descrição Antiga", BigDecimal.TEN, null);
        Product updatedProduct = new Product(productId, "Produto Atualizado", "Descrição Atualizada", BigDecimal.valueOf(2000), null);

        when(repository.findById(productId)).thenReturn(java.util.Optional.of(existingProduct));
        when(repository.save(any())).thenReturn(updatedProduct);

        ProductResponse response = productService.update(productId, request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(updatedProduct.getId());
        assertThat(response.getName()).isEqualTo(updatedProduct.getName());
        assertThat(response.getDescription()).isEqualTo(updatedProduct.getDescription());
        assertThat(response.getPrice()).isEqualTo(updatedProduct.getPrice());
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        Long productId = 1L;

        when(repository.existsById(productId)).thenReturn(true);

        productService.delete(productId);

        when(repository.existsById(productId)).thenReturn(false);
    }

    @Test
    void delete_WhenProductNotExists_ShouldThrowException() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
