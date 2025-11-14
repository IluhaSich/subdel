package com.example.subdel.graphql;

import com.example.subdel.service.DelicacyService;
import com.example.subdel_api.dtos.request.DelicacyRequest;
import com.example.subdel_api.dtos.response.DelicacyResponse;
import com.example.subdel_api.dtos.response.PagedResponse;
import com.example.subdel_api.dtos.response.ProductResponse;
import com.netflix.graphql.dgs.*;

import java.util.List;
import java.util.Map;

@DgsComponent
public class DelicacyDataFetcher {

    private final DelicacyService delicacyService;

    public DelicacyDataFetcher(DelicacyService delicacyService) {
        this.delicacyService = delicacyService;
    }

    @DgsQuery
    public PagedResponse<DelicacyResponse> delicacies(
            @InputArgument Long productId,
            @InputArgument int page,
            @InputArgument int size
    ) {
        return delicacyService.findAllDelicacies(productId, page, size);
    }

    @DgsQuery
    public DelicacyResponse delicacyById(@InputArgument Long id) {
        return delicacyService.findDelicacyById(id);
    }

    @DgsMutation
    public DelicacyResponse createDelicacy(@InputArgument("input") Map<String, Object> input) {

        DelicacyRequest request = mapToDelicacyRequest(input);

        return delicacyService.createDelicacy(request);
    }

    @DgsMutation
    public DelicacyResponse updateDelicacy(
            @InputArgument Long id,
            @InputArgument("input") Map<String, Object> input
    ) {

        DelicacyRequest request = mapToDelicacyRequest(input);

        return delicacyService.updateDelicacy(id, request);
    }

    @DgsMutation
    public Long deleteDelicacy(@InputArgument Long id) {
        delicacyService.deleteDelicacy(id);
        return id;
    }

    @DgsData(parentType = "Delicacy", field = "products")
    public List<?> products(DgsDataFetchingEnvironment dfe) {
        DelicacyResponse delicacy = dfe.getSource();
        return delicacy.getProducts();
    }

    private DelicacyRequest mapToDelicacyRequest(Map<String, Object> input) {

        List<Long> productIds = ((List<?>) input.get("productIds")).stream()
                .map(id -> Long.parseLong(id.toString()))
                .toList();

        List<ProductResponse> products = productIds.stream()
                .map(id -> new ProductResponse(id, null))
                .toList();

        return new DelicacyRequest(
                (String) input.get("name"),
                ((Number) input.get("price")).doubleValue(),
                ((Number) input.get("mass")).doubleValue(),
                ((Number) input.get("proteins")).doubleValue(),
                ((Number) input.get("fats")).doubleValue(),
                ((Number) input.get("carbohydrates")).doubleValue(),
                ((Number) input.get("kcal")).doubleValue(),
                (String) input.get("country"),
                products
        );
    }


}
