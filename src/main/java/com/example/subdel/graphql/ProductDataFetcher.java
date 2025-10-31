package com.example.subdel.graphql;

import com.example.subdel.service.ProductService;
import com.example.subdel_api.dtos.request.ProductRequest;
import com.example.subdel_api.dtos.response.ProductResponse;
import com.netflix.graphql.dgs.*;

import java.util.List;


@DgsComponent
public class ProductDataFetcher {

    private final ProductService productService;

    public ProductDataFetcher(ProductService productService) {
        this.productService = productService;
    }

    @DgsQuery
    public List<ProductResponse> products() {
        return productService.findAllProducts();
    }

    @DgsQuery
    public ProductResponse productById(@InputArgument Long id) {
        return productService.findById(id);
    }

    @DgsMutation
    public ProductResponse createProduct(@InputArgument("input") ProductRequest input) {
        return productService.create(input);
    }

    @DgsMutation
    public ProductResponse updateProduct(@InputArgument Long id, @InputArgument("input") ProductRequest input) {
        return productService.update(id, input);
    }

    @DgsMutation
    public Long deleteProduct(@InputArgument Long id) {
        productService.delete(id);
        return id;
    }
}
