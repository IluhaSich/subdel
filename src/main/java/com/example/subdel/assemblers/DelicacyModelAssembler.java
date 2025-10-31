package com.example.subdel.assemblers;

import com.example.subdel.controller.DelicacyController;
import com.example.subdel.controller.ProductController;
import com.example.subdel_api.dtos.response.DelicacyResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DelicacyModelAssembler
        implements RepresentationModelAssembler<DelicacyResponse, EntityModel<DelicacyResponse>> {

    @Override
    public EntityModel<DelicacyResponse> toModel(DelicacyResponse entity) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(DelicacyController.class)
                .getDelicacyById(entity.getId())).withSelfRel());

        if (entity.getProducts() != null) {
            entity.getProducts().forEach(productResponse ->
                    links.add(linkTo(methodOn(ProductController.class)
                            .getProductById(productResponse.getId()))
                            .withRel(productResponse.getName()))
            );
        }

        links.add(linkTo(methodOn(DelicacyController.class)
                .getAllDelicacies(null,0,10)).withRel("collection"));


        return EntityModel.of(entity,links);
    }

    @Override
    public CollectionModel<EntityModel<DelicacyResponse>> toCollectionModel(Iterable<? extends DelicacyResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
