package com.example.orderservice.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderNumberGeneratorService {

    private final EntityManager entityManager;

    @Transactional
    public long getNextOrderNumber() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('order_number_seq')")
                .getSingleResult()).longValue();
    }
}
