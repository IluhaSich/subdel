package com.example.subdel.controller;

import com.example.subdel_api.dtos.request.UserRequest;
import com.example.subdel_api.dtos.response.UserResponse;
import com.example.subdel_api.endpoints.UserApi;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserApi {

    @Override
    public PagedModel<EntityModel<UserResponse>> getAllUsers(Long userId, int page, int size) {
        return null;
    }

    @Override
    public EntityModel<UserResponse> getUserById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<EntityModel<UserResponse>> createUser(@Valid UserRequest request) {
        return null;
    }

    @Override
    public EntityModel<UserResponse> updateUser(Long id, @Valid UserRequest request) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }
}
