package com.example.subdel.graphql;

import com.example.subdel.service.ProductService;
import com.example.subdel_api.dtos.request.ProductRequest;
import com.example.subdel_api.dtos.response.ProductResponse;
import com.netflix.graphql.dgs.*;

import java.util.List;
import java.util.Map;


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
    public ProductResponse createProduct(@InputArgument("input") Map<String, String> input) {

        ProductRequest request = new ProductRequest( input.get("name"), Double.parseDouble(input.get("price")));
        return productService.create(request);
    }

    @DgsMutation
    public ProductResponse updateProduct(
            @InputArgument Long id,
            @InputArgument("input") Map<String, String> input
    ) {

        ProductRequest request = new ProductRequest( input.get("name"), Double.parseDouble(input.get("price")));

        return productService.update(id, request);
    }

    @DgsMutation
    public Long deleteProduct(@InputArgument Long id) {
        productService.delete(id);
        return id;
    }
}
