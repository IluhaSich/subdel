package com.example.subdel.assemblers;

import com.example.subdel.controller.DelicacyController;
import com.example.subdel.controller.ProductController;
import com.example.subdel_api.dtos.response.ProductResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductModelAssembler
        implements RepresentationModelAssembler<ProductResponse, EntityModel<ProductResponse>> {
    @Override
    public EntityModel<ProductResponse> toModel(ProductResponse entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(ProductController.class).getProductById(entity.getId())).withSelfRel(),
                linkTo(methodOn(DelicacyController.class)
                        // можно добавить .expend чтобы ссылка не содержала {&delicacyId} ->
                        //      -> "http://localhost:8080/api/delicacies?page=0&size=10{&delicacyId}"
                        .getAllDelicacies(null,0,10)).withRel("delicacies"),
                linkTo(methodOn(ProductController.class).getAllProducts()).withRel("collection")
        );
    }
//    Пример
//    @Override
//    public EntityModel<AuthorResponse> toModel(AuthorResponse author) {
//        return EntityModel.of(author,
//                linkTo(methodOn(AuthorController.class).getAuthorById(author.getId())).withSelfRel(),
//                linkTo(methodOn(BookController.class).getAllBooks(author.getId(), 0, 10)).withRel("books"),
//                linkTo(methodOn(AuthorController.class).getAllAuthors()).withRel("collection")
//        );
//    }

    @Override
    public CollectionModel<EntityModel<ProductResponse>> toCollectionModel(Iterable<? extends ProductResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
