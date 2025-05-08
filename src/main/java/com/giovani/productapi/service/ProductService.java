package com.giovani.productapi.service;

import com.giovani.productapi.dto.ProductRequest;
import com.giovani.productapi.dto.ProductResponse;
import com.giovani.productapi.entity.Product;
import com.giovani.productapi.exceptions.ResourceNotFoundException;
import com.giovani.productapi.mapper.ProductMapper;
import com.giovani.productapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.giovani.productapi.mapper.ProductMapper.toEntity;
import static com.giovani.productapi.mapper.ProductMapper.toResponse;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public ProductResponse create(ProductRequest request) {
        Product product = toEntity(request);
        return toResponse(repository.save(product));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse findById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
        return toResponse(product);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        return toResponse(repository.save(product));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}