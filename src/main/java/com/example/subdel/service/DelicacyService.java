package com.example.subdel.service;

import com.example.events_contract.events.ProductDto;
import com.example.events_contract.events.DelicacyCreatedEvent;
import com.example.subdel.config.RabbitMQConfig;
import com.example.subdel.storage.InMemoryStorage;
import com.example.subdel_api.dtos.request.DelicacyRequest;
import com.example.subdel_api.dtos.response.DelicacyResponse;
import com.example.subdel_api.dtos.response.PagedResponse;
import com.example.subdel_api.dtos.response.ProductResponse;
import com.example.subdel_api.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class DelicacyService {

    private static final Logger log = LoggerFactory.getLogger(DelicacyService.class);

    private final InMemoryStorage storage;
    private final ProductService productService;
    private final RabbitTemplate rabbitTemplate;

    public DelicacyService(InMemoryStorage storage, @Lazy ProductService productService, RabbitTemplate rabbitTemplate) {
        this.storage = storage;
        this.productService = productService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public DelicacyResponse findDelicacyById(Long id) {
        return Optional.ofNullable(storage.delicacies.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Delicacy", id));
    }

    public PagedResponse<DelicacyResponse> findAllDelicacies(Long delicacyId, int page, int size) {
        Stream<DelicacyResponse> delicacyStream = storage.delicacies.values().stream()
                .sorted((d1, d2) -> d1.getId().compareTo(d2.getId()));

        if (delicacyId != null) {
            delicacyStream = delicacyStream.filter(delicacy ->
                    delicacy.getProducts() != null &&
                            delicacy.getProducts().stream().anyMatch(p -> p.getId().equals(delicacyId))
            );
        }

        List<DelicacyResponse> allDelicacies = delicacyStream.toList();

        int totalElements = allDelicacies.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<DelicacyResponse> pageContent = (fromIndex > toIndex) ? List.of() : allDelicacies.subList(fromIndex, toIndex);

        return new PagedResponse<>(pageContent, page, size, totalElements, totalPages, page >= totalPages - 1);
    }

    public DelicacyResponse createDelicacy(DelicacyRequest request) {
        List<ProductResponse> productList = request.products().stream()
                .map(p -> productService.findById(p.getId()))
                .toList();

        long id = storage.delicacySequence.incrementAndGet();
        var delicacy = new DelicacyResponse(
                id,
                request.name(),
                null,
                request.mass(),
                request.proteins(),
                request.fats(),
                request.carbohydrates(),
                request.kcal(),
                request.country(),
                productList,
                LocalDateTime.now()
        );

        storage.delicacies.put(id, delicacy);
        log.info("delicacy was saved: {}.", delicacy);

        DelicacyCreatedEvent event = new DelicacyCreatedEvent(
                delicacy.getId(),
                delicacy.getProducts().stream().map(productResponse ->
                        new ProductDto(productResponse.getId(),
                                productResponse.getName(),
                                productResponse.getPrice()
                )).toList()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_BOOK_CREATED, event);
        return delicacy;
    }

    public DelicacyResponse updateDelicacy(Long id, DelicacyRequest request) {
        DelicacyResponse existing = findDelicacyById(id);


        List<ProductResponse> productList = request.products().stream()
                .map(p -> productService.findById(p.getId()))
                .toList();

        var updated = new DelicacyResponse(
                id,
                request.name(),
                null,
                request.mass(),
                request.proteins(),
                request.fats(),
                request.carbohydrates(),
                request.kcal(),
                request.country(),
                productList,
                existing.getCreatedAt()
        );

        storage.delicacies.put(id, updated);
        return updated;
    }

    public void deleteDelicacy(Long id) {
        findDelicacyById(id);
        storage.delicacies.remove(id);
    }

    public void deleteDelicaciesByProductId(Long productId) {
        List<Long> toDelete = storage.delicacies.values().stream()
                .filter(d -> d.getProducts() != null
                        && d.getProducts().stream().anyMatch(p -> p.getId().equals(productId)))
                .map(DelicacyResponse::getId)
                .toList();

        toDelete.forEach(storage.delicacies::remove);
    }
}