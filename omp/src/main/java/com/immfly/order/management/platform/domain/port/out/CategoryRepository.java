package com.immfly.order.management.platform.domain.port.out;

import com.immfly.order.management.platform.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    void save(Category category);

    Optional<Category> findById(UUID id);

    void update(Category category);

    void deleteById(UUID id);

    List<Category> findAll();
}