package com.immfly.order.management.platform.domain.port.out;

import com.immfly.order.management.platform.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    void save(Order order);

    Optional<Order> findById(UUID id);

    void update(Order order);

    void deleteById(UUID id);
}
