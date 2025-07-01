package com.immfly.order.management.platform.domain.port.out;

import com.immfly.order.management.platform.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    void save(Product product);

    Optional<Product> findById(UUID id);

    void update(Product product);

    void deleteById(UUID id);

    List<Product> findAll();
}