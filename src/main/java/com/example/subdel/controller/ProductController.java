package com.example.subdel.controller;

import com.example.subdel.assemblers.ProductModelAssembler;
import com.example.subdel.service.ProductService;
import com.example.subdel_api.dtos.request.ProductRequest;
import com.example.subdel_api.dtos.response.ProductResponse;
import com.example.subdel_api.endpoints.ProductApi;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController implements ProductApi {

    private final ProductService productService;
    private final ProductModelAssembler productModelAssembler;

    public ProductController(ProductService productService,
                             ProductModelAssembler productModelAssembler) {
        this.productService = productService;
        this.productModelAssembler = productModelAssembler;
    }

    @Override
    public CollectionModel<EntityModel<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.findAllProducts();
        return productModelAssembler.toCollectionModel(products);
    }

    @Override
    public EntityModel<ProductResponse> getProductById(Long id) {
        ProductResponse product = productService.findById(id);
        return productModelAssembler.toModel(product);
    }

    @Override
    public ResponseEntity<EntityModel<ProductResponse>> createProduct(@Valid ProductRequest request) {
        ProductResponse created = productService.create(request);
        EntityModel<ProductResponse> model = productModelAssembler.toModel(created);

        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    public EntityModel<ProductResponse> updateProduct(Long id, @Valid ProductRequest request) {
        ProductResponse updated = productService.update(id, request);
        return productModelAssembler.toModel(updated);
    }

    @Override
    public void deleteProduct(Long id) {
        productService.delete(id);
    }

}
