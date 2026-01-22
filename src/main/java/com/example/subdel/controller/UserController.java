package com.example.subdel.controller;

import com.example.subdel.assemblers.UserModelAssembler;
import com.example.subdel.service.UserService;
import com.example.subdel_api.dtos.request.UserRequest;
import com.example.subdel_api.dtos.response.PagedResponse;
import com.example.subdel_api.dtos.response.UserResponse;
import com.example.subdel_api.endpoints.UserApi;
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
public class UserController implements UserApi {

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;
    private final PagedResourcesAssembler<UserResponse> pagedResourcesAssembler;

    public UserController(UserService userService,
                          UserModelAssembler userModelAssembler,
                          PagedResourcesAssembler<UserResponse> pagedResourcesAssembler) {
        this.userService = userService;
        this.userModelAssembler = userModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    public PagedModel<EntityModel<UserResponse>> getAllUsers(Long userId, int page, int size) {
        PagedResponse<UserResponse> pagedResponse = userService.findAll(userId, page, size);

        Page<UserResponse> userPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );

        return pagedResourcesAssembler.toModel(userPage, userModelAssembler);
    }

    @Override
    public EntityModel<UserResponse> getUserById(Long id) {
        return userModelAssembler.toModel(userService.findById(id));
    }

    @Override
    public ResponseEntity<EntityModel<UserResponse>> createUser(@Valid UserRequest request) {
        UserResponse created = userService.create(request);
        EntityModel<UserResponse> model = userModelAssembler.toModel(created);

        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    public EntityModel<UserResponse> updateUser(Long id, @Valid UserRequest request) {
        return userModelAssembler.toModel(userService.update(id, request));
    }

    @Override
    public void deleteUser(Long id) {
        userService.delete(id);
    }
}
