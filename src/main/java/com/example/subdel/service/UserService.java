package com.example.subdel.service;

import com.example.subdel.storage.InMemoryStorage;
import com.example.subdel_api.dtos.request.UserRequest;
import com.example.subdel_api.dtos.response.DelicacyResponse;
import com.example.subdel_api.dtos.response.PagedResponse;
import com.example.subdel_api.dtos.response.UserResponse;
import com.example.subdel_api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserService {

    private final InMemoryStorage storage;
    private final DelicacyService delicacyService;

    public UserService(InMemoryStorage storage, DelicacyService delicacyService) {
        this.storage = storage;
        this.delicacyService = delicacyService;
    }

    public UserResponse findById(Long id) {
        return Optional.ofNullable(storage.users.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public PagedResponse<UserResponse> findAll(Long userId, int page, int size) {
        Stream<UserResponse> stream = storage.users.values().stream()
                .sorted((u1, u2) -> u1.getId().compareTo(u2.getId()));

        if (userId != null) {
            stream = stream.filter(u -> u.getId().equals(userId));
        }

        List<UserResponse> all = stream.toList();

        int totalElements = all.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int from = page * size;
        int to = Math.min(from + size, totalElements);

        List<UserResponse> content =
                from > to ? List.of() : all.subList(from, to);

        return new PagedResponse<>(content, page, size, totalElements, totalPages, page >= totalPages - 1);
    }

    public UserResponse create(UserRequest request) {
        long id = storage.userSequence.incrementAndGet();

        List<DelicacyResponse> delicacies = request.delicacies() == null
                ? List.of()
                : request.delicacies().stream()
                .map(d -> delicacyService.findDelicacyById(d.getId()))
                .toList();

        UserResponse user = new UserResponse(id, request.name(), delicacies);
        storage.users.put(id, user);
        return user;
    }

    public UserResponse update(Long id, UserRequest request) {
        findById(id);

        List<DelicacyResponse> delicacies = request.delicacies() == null
                ? List.of()
                : request.delicacies().stream()
                .map(d -> delicacyService.findDelicacyById(d.getId()))
                .toList();

        UserResponse updated = new UserResponse(id, request.name(), delicacies);
        storage.users.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        findById(id);
        storage.users.remove(id);
    }
}
