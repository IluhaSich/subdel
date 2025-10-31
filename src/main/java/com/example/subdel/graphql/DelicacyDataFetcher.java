package com.example.subdel.graphql;

import com.example.subdel.service.DelicacyService;
import com.example.subdel_api.dtos.request.DelicacyRequest;
import com.example.subdel_api.dtos.response.DelicacyResponse;
import com.example.subdel_api.dtos.response.PagedResponse;
import com.netflix.graphql.dgs.*;

import java.util.List;

@DgsComponent
public class DelicacyDataFetcher {

    private final DelicacyService delicacyService;

    public DelicacyDataFetcher(DelicacyService delicacyService) {
        this.delicacyService = delicacyService;
    }

    // ======== QUERIES ========

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
    public DelicacyResponse createDelicacy(@InputArgument("input") DelicacyRequest input) {
        return delicacyService.createDelicacy(input);
    }

    @DgsMutation
    public DelicacyResponse updateDelicacy(@InputArgument Long id, @InputArgument("input") DelicacyRequest input) {
        return delicacyService.updateDelicacy(id, input);
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
}
