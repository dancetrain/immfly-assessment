package com.immfly.order.management.platform.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immfly.order.management.platform.ContainerPostgresSQLTest;
import com.immfly.order.management.platform.OrderManagementSystemTest;
import com.immfly.order.management.platform.adapter.out.payment.MockPaymentGateway;
import com.immfly.order.management.platform.domain.model.Order;
import com.immfly.order.management.platform.domain.model.OrderStatus;
import com.immfly.order.management.platform.domain.model.Product;
import com.immfly.order.management.platform.domain.port.out.OrderRepository;
import com.immfly.order.management.platform.domain.port.out.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
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
    private ProductRepository productRepository;

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

    @Test
    void shouldUpdateOrderWithProducts() throws Exception {
        // Step 1: Setup test data
        String createPayload = """
            {"seatLetter":"B","seatNumber":22}
            """;
        String createdJson = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andReturn().getResponse().getContentAsString();

        String orderId = objectMapper.readTree(createdJson).get("id").asText();

        Product product1 = new Product(UUID.randomUUID(), "Product 1", BigDecimal.valueOf(10.0), null, null, 6);
        Product product2 = new Product(UUID.randomUUID(), "Product 2", BigDecimal.valueOf(20.0), null, null, 10);
        productRepository.save(product1);
        productRepository.save(product2);

        // Step 2: Update with buyer email and products
        String updatePayload = format("""
            {
                "buyerEmail":"test@example.com",
                "productsQty": {"%s": %d, "%s": %d}
            }
            """, product1.getId(), product1.getStock(), product2.getId(), product2.getStock());

        double expectedTotalPrice = product1.getStock() * product1.getPrice().doubleValue() +
                product2.getStock() * product2.getPrice().doubleValue();

        mockMvc.perform(put("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyerEmail").value("test@example.com"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.productsQty."+ product1.getId()).value( product1.getStock()))
                .andExpect(jsonPath("$.productsQty."+ product2.getId()).value( product2.getStock()))
                .andExpect(jsonPath("$.totalPrice").value(expectedTotalPrice));
    }

    private String createOrderAndGetId() throws Exception {
        String payload = """
        {"seatLetter":"E","seatNumber":55}
        """;

        String response = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    @Test
    void shouldFinishOrder() throws Exception {
        String orderId = createOrderAndGetId();

        String finishPayload = """
            {
                "cardToken": "mock-card",
                "paymentGateway": "mock-success"
            }
            """;

        mockMvc.perform(post("/orders/{id}/finish", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(finishPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINISHED"))
                .andExpect(jsonPath("$.paymentStatus").exists());
    }

    @Test
    void finishOrderReturnsFailed() throws Exception {
        String orderId = createOrderAndGetId();

        String finishPayload = """
        {
            "cardToken": "mock-card",
            "paymentGateway": "mock-fail"
        }
        """;

        mockMvc.perform(post("/orders/{id}/finish", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(finishPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINISHED"))
                .andExpect(jsonPath("$.paymentStatus").value("PaymentFailed"));
    }

    @Test
    void finishOrderReturnsOffline() throws Exception {
        String orderId = createOrderAndGetId();

        String finishPayload = """
        {
            "cardToken": "mock-card",
            "paymentGateway": "mock-offline"
        }
        """;

        mockMvc.perform(post("/orders/{id}/finish", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(finishPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINISHED"))
                .andExpect(jsonPath("$.paymentStatus").value("OfflinePayment"));
    }


    @Test
    void finishOrderReturnsPaidForVisa() throws Exception {
        String orderId = createOrderAndGetId();

        String finishPayload = """
        {
            "cardToken": "mock-card",
            "paymentGateway": "visa"
        }
        """;

        mockMvc.perform(post("/orders/{id}/finish", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(finishPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINISHED"))
                .andExpect(jsonPath("$.paymentStatus").value("Paid"));
    }
}
