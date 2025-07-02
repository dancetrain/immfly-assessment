package com.immfly.order.management.platform.adapter.out.persistence;

import com.immfly.order.management.platform.ContainerPostgresSQLTest;
import com.immfly.order.management.platform.OrderManagementSystemTest;
import com.immfly.order.management.platform.domain.model.Category;
import com.immfly.order.management.platform.domain.port.out.CategoryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@OrderManagementSystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcCategoryRepositoryTest extends ContainerPostgresSQLTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private static UUID savedCategoryId;

    @Test
    @Order(1)
    void saveAndFindById() {
        UUID id = UUID.randomUUID();
        Category category = new Category(id, "Food", null);

        categoryRepository.save(category);

        Optional<Category> fetched = categoryRepository.findById(id);
        assertThat(fetched).isPresent();
        assertThat(fetched.get()).isEqualTo(category);

        // Remember ID for update/delete
        savedCategoryId = id;
    }

    @Test
    @Order(2)
    void updateCategory() {
        Category updated = new Category(savedCategoryId, "Food / Beverages", null);
        categoryRepository.update(updated);

        Optional<Category> fetched = categoryRepository.findById(savedCategoryId);
        assertThat(fetched).isPresent();
        assertThat(fetched.get()).isEqualTo(updated);
    }

    @Test
    @Order(3)
    void findAll() {
        List<Category> all = categoryRepository.findAll();
        assertThat(all).isNotEmpty();
    }

    @Test
    @Order(4)
    void deleteById() {
        categoryRepository.deleteById(savedCategoryId);

        Optional<Category> fetched = categoryRepository.findById(savedCategoryId);
        assertThat(fetched).isEmpty();
    }
}