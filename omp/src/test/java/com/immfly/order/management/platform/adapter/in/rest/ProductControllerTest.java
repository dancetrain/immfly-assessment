package com.immfly.order.management.platform.adapter.in.rest;

import com.immfly.order.management.platform.ContainerPostgresSQLTest;
import com.immfly.order.management.platform.OrderManagementSystemTest;
import com.immfly.order.management.platform.domain.model.Product;
import com.immfly.order.management.platform.domain.port.out.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@OrderManagementSystemTest
@AutoConfigureMockMvc
public class ProductControllerTest extends ContainerPostgresSQLTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    private UUID savedProductId;

    @BeforeEach
    void setUp() {
        // Clean any existing test data
        productRepository.findAll()
                .forEach(p -> productRepository.deleteById(p.getId()));

        // Insert a product for this test
        savedProductId = UUID.randomUUID();
        Product product = new Product(
                savedProductId,
                "TestProduct",
                BigDecimal.valueOf(29.99),
                null,
                "https://example.com/image.jpg",
                100
        );
        productRepository.save(product);
    }

    @Test
    void shouldReturnListOfProducts() throws Exception {
        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedProductId.toString()))
                .andExpect(jsonPath("$[0].name").value("TestProduct"))
                .andExpect(jsonPath("$[0].price").value(29.99))
                .andExpect(jsonPath("$[0].image").value("https://example.com/image.jpg"))
                .andExpect(jsonPath("$[0].stock").value(100));
    }
}
