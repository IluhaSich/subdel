package com.example.subdel.service;

import com.example.subdel.storage.InMemoryStorage;
import com.example.subdel_api.dtos.request.ProductRequest;
import com.example.subdel_api.dtos.response.ProductResponse;
import com.example.subdel_api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final InMemoryStorage storage;

    public ProductService(InMemoryStorage storage) {
        this.storage = storage;
    }

    public List<ProductResponse> findAllProducts() {
        return storage.products.values().stream().toList();
    }

    public ProductResponse findById(Long id) {
        return Optional.ofNullable(storage.products.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Продукт", id));
    }

    public ProductResponse create(ProductRequest request) {
        long id = storage.productSequence.incrementAndGet();
        ProductResponse product = new ProductResponse(id, request.name());
        storage.products.put(id, product);
        return product;
    }

    public ProductResponse update(Long id, ProductRequest request) {
        findById(id);
        ProductResponse updated = new ProductResponse(id, request.name());
        storage.products.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        findById(id);
        storage.products.remove(id);
    }
}