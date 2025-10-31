package com.example.subdel.controller;

import com.example.subdel.assemblers.DelicacyModelAssembler;
import com.example.subdel.service.DelicacyService;
import com.example.subdel_api.dtos.request.DelicacyRequest;
import com.example.subdel_api.dtos.response.DelicacyResponse;
import com.example.subdel_api.dtos.response.PagedResponse;
import com.example.subdel_api.endpoints.DelicacyApi;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DelicacyController implements DelicacyApi {

    private final DelicacyService delicacyService;
    private final DelicacyModelAssembler delicacyModelAssembler;
    private final PagedResourcesAssembler<DelicacyResponse> pagedResourcesAssembler;

    public DelicacyController(DelicacyService delicacyService,
                              DelicacyModelAssembler delicacyModelAssembler,
                              PagedResourcesAssembler<DelicacyResponse> pagedResourcesAssembler) {
        this.delicacyService = delicacyService;
        this.delicacyModelAssembler = delicacyModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    public PagedModel<EntityModel<DelicacyResponse>> getAllDelicacies(Long delicacyId, int page, int size) {
        PagedResponse<DelicacyResponse> pagedResponse = delicacyService.findAllDelicacies(delicacyId, page, size);

        Page<DelicacyResponse> delicacyPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );

        return pagedResourcesAssembler.toModel(delicacyPage, delicacyModelAssembler);
    }

    @Override
    public EntityModel<DelicacyResponse> getDelicacyById(Long id) {
        DelicacyResponse response = delicacyService.findDelicacyById(id);
        return delicacyModelAssembler.toModel(response);
    }

    @Override
    public ResponseEntity<EntityModel<DelicacyResponse>> createDelicacy(@Valid DelicacyRequest request) {
        DelicacyResponse created = delicacyService.createDelicacy(request);
        EntityModel<DelicacyResponse> model = delicacyModelAssembler.toModel(created);

        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    public EntityModel<DelicacyResponse> updateDelicacy(Long id, @Valid DelicacyRequest request) {
        DelicacyResponse updated = delicacyService.updateDelicacy(id, request);
        return delicacyModelAssembler.toModel(updated);
    }

    @Override
    public void deleteDelicacy(Long id) {
        delicacyService.deleteDelicacy(id);
    }

}
