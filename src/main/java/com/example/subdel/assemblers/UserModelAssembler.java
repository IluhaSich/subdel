package com.example.subdel.assemblers;

import com.example.subdel.controller.DelicacyController;
import com.example.subdel.controller.UserController;
import com.example.subdel_api.dtos.response.UserResponse;
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
public class UserModelAssembler
        implements RepresentationModelAssembler<UserResponse, EntityModel<UserResponse>> {

    @Override
    public EntityModel<UserResponse> toModel(UserResponse entity) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(UserController.class)
                .getUserById(entity.getId()))
                .withSelfRel());

        if (entity.getDelicacies() != null) {
            entity.getDelicacies().forEach(delicacy ->
                    links.add(linkTo(methodOn(DelicacyController.class)
                            .getDelicacyById(delicacy.getId()))
                            .withRel(delicacy.getName()))
            );
        }

        links.add(linkTo(methodOn(UserController.class)
                .getAllUsers(null, 0, 10))
                .withRel("collection"));

        return EntityModel.of(entity, links);
    }

    @Override
    public CollectionModel<EntityModel<UserResponse>> toCollectionModel(
            Iterable<? extends UserResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
