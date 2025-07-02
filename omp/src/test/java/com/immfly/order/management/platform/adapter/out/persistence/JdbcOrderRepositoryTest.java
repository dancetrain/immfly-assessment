package com.immfly.order.management.platform.adapter.out.persistence;

import com.immfly.order.management.platform.ContainerPostgresSQLTest;
import com.immfly.order.management.platform.OrderManagementSystemTest;
import com.immfly.order.management.platform.domain.model.Order;
import com.immfly.order.management.platform.domain.model.OrderStatus;
import com.immfly.order.management.platform.domain.port.out.OrderRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@OrderManagementSystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcOrderRepositoryTest extends ContainerPostgresSQLTest {

    @Autowired
    private OrderRepository orderRepository;

    private static UUID orderId;

    @Test
    @org.junit.jupiter.api.Order(1)
    void saveAndFindById() {
        orderId = UUID.randomUUID();

        Order order = new Order(
                orderId,
                "A",
                11,
                OrderStatus.OPEN,
                "test@example.com",
                Map.of(UUID.randomUUID(), 10),
                0.0,
                null,
                null,
                null
        );

        orderRepository.save(order);

        Optional<Order> fetched = orderRepository.findById(orderId);
        assertThat(fetched).isPresent();
        assertThat(fetched.get()).isEqualTo(order);
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void updateOrder() {
        Order updated = new Order(
                orderId,
                "B",
                15,
                OrderStatus.FINISHED,
                "updated@example.com",
                Map.of(UUID.randomUUID(), 1),
                99.99,
                "PAID",
                LocalDateTime.now(),
                "mock-gateway"
        );

        orderRepository.update(updated);

        Optional<Order> fetched = orderRepository.findById(orderId);
        assertThat(fetched).isPresent();
        assertThat(fetched.get()).isEqualTo(updated);
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void deleteById() {
        orderRepository.deleteById(orderId);

        Optional<Order> fetched = orderRepository.findById(orderId);
        assertThat(fetched).isEmpty();
    }
}