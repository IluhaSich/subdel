package com.example.subdel.storage;

import com.example.subdel_api.dtos.response.DelicacyResponse;
import com.example.subdel_api.dtos.response.ProductResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {

    public final Map<Long, DelicacyResponse> delicacies = new ConcurrentHashMap<>();
    public final Map<Long, ProductResponse> products = new ConcurrentHashMap<>();

    public final AtomicLong delicacySequence = new AtomicLong(0);
    public final AtomicLong productSequence = new AtomicLong(0);

    @PostConstruct
    public void init() {

        // Создание продуктов
        var product1 = new ProductResponse(productSequence.incrementAndGet(), "Морковь", 5.0);
        var product2 = new ProductResponse(productSequence.incrementAndGet(), "Перец", 6.27);
        var product3 = new ProductResponse(productSequence.incrementAndGet(), "Клубника", 30.0);
        var product4 = new ProductResponse(productSequence.incrementAndGet(), "Молоко", 8.7);

        products.put(product1.getId(), product1);
        products.put(product2.getId(), product2);
        products.put(product3.getId(), product3);
        products.put(product4.getId(), product4);

        // Создание деликатесов
        var delicacyId1 = delicacySequence.incrementAndGet();
        delicacies.put(delicacyId1, new DelicacyResponse(
                delicacyId1,
                "Фруктовый салат",
                250.0,
                300.0,
                2.5,
                1.0,
                15.0,
                80.0,
                "Россия",
                List.of(product3),
                LocalDateTime.now()
        ));

        var delicacyId2 = delicacySequence.incrementAndGet();
        delicacies.put(delicacyId2, new DelicacyResponse(
                delicacyId2,
                "Овощной микс",
                180.0,
                400.0,
                3.0,
                0.5,
                10.0,
                60.0,
                "Беларусь",
                List.of(product1, product2),
                LocalDateTime.now()
        ));

        var delicacyId3 = delicacySequence.incrementAndGet();
        delicacies.put(delicacyId3, new DelicacyResponse(
                delicacyId3,
                "Молочный коктейль с клубникой",
                320.0,
                250.0,
                4.0,
                3.5,
                20.0,
                120.0,
                "Казахстан",
                List.of(product3, product4),
                LocalDateTime.now()
        ));
    }
}
