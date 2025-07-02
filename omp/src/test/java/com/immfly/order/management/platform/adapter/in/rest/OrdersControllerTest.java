package com.immfly.order.management.platform.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immfly.order.management.platform.ContainerPostgresSQLTest;
import com.immfly.order.management.platform.OrderManagementSystemTest;
import com.immfly.order.management.platform.domain.model.Order;
import com.immfly.order.management.platform.domain.model.OrderStatus;
import com.immfly.order.management.platform.domain.port.out.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@OrderManagementSystemTest
@AutoConfigureMockMvc
public class OrdersControllerTest extends ContainerPostgresSQLTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID createdOrderId;

    @Test
    void shouldCreateAndGetOrder() throws Exception {
        // Create Order
        String payload = """
            {
                "seatLetter": "A",
                "seatNumber": 11
            }
            """;

        String response = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn().getResponse().getContentAsString();

        // Parse ID
        createdOrderId = UUID.fromString(objectMapper.readTree(response).get("id").asText());
        assertThat(createdOrderId).isNotNull();

        // Fetch order
        mockMvc.perform(get("/orders/{id}", createdOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatLetter").value("A"))
                .andExpect(jsonPath("$.seatNumber").value(11))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void shouldCancelOrder() throws Exception {
        // Step 1: Create
        String createPayload = """
            {"seatLetter":"D","seatNumber":42}
            """;
        String createdJson = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andReturn().getResponse().getContentAsString();

        String orderId = objectMapper.readTree(createdJson).get("id").asText();

        // Step 2: Cancel
        mockMvc.perform(delete("/orders/{id}", orderId))
                .andExpect(status().isNoContent());

        // Verify in repo
        Order canceled = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();
        assertThat(canceled.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }
}
