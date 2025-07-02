package com.immfly.order.management.platform.adapter.out.persistence;

import com.immfly.order.management.platform.ContainerPostgresSQLTest;
import com.immfly.order.management.platform.OrderManagementSystemTest;
import com.immfly.order.management.platform.domain.model.Product;
import com.immfly.order.management.platform.domain.port.out.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@OrderManagementSystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JdbcProductRepositoryTest extends ContainerPostgresSQLTest {

    @Autowired
    private ProductRepository productRepository;

    private static UUID productId;

    @Test
    @Order(1)
    void saveAndFindById() {
        productId = UUID.randomUUID();
        Product product = new Product(
                productId,
                "Tuna Sandwich",
                BigDecimal.valueOf(19.99),
                null,
                "tuna-sandwich-image-url",
                10
        );

        productRepository.save(product);

        Optional<Product> fetched = productRepository.findById(productId);
        assertThat(fetched).isPresent();
        assertThat(fetched.get()).isEqualTo(product);
    }

    @Test
    @Order(2)
    void updateProduct() {
        Product updated = new Product(
                productId,
                "Chicken Sandwich",
                BigDecimal.valueOf(29.99),
                null,
                "chicken-sandwich-image-url",
                15
        );

        productRepository.update(updated);

        Optional<Product> fetched = productRepository.findById(productId);
        assertThat(fetched).isPresent();
        assertThat(fetched.get()).isEqualTo(updated);
    }

    @Test
    @Order(3)
    void findAll() {
        List<Product> all = productRepository.findAll();
        assertThat(all).isNotEmpty();
    }

    @Test
    @Order(4)
    void deleteById() {
        productRepository.deleteById(productId);

        Optional<Product> fetched = productRepository.findById(productId);
        assertThat(fetched).isEmpty();
    }
}
