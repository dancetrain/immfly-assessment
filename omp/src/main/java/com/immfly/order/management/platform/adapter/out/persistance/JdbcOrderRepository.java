package com.immfly.order.management.platform.adapter.out.persistance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immfly.order.management.platform.domain.model.Order;
import com.immfly.order.management.platform.domain.model.OrderStatus;
import com.immfly.order.management.platform.domain.port.out.OrderRepository;
import org.postgresql.util.PGobject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper;

    public JdbcOrderRepository(NamedParameterJdbcTemplate jdbcTemplate,
                               ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(Order order) {
        String sql = """
                INSERT INTO orders (
                    id, seat_letter, seat_number, status, buyer_email,
                    products_qty, total_price, payment_status, payment_date, payment_gateway
                ) VALUES (
                    :id, :seatLetter, :seatNumber, :status, :buyerEmail,
                    :productsQty, :totalPrice, :paymentStatus, :paymentDate, :paymentGateway
                )
                """;

        MapSqlParameterSource params = mapOrderToParams(order);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        String sql = "SELECT * FROM orders WHERE id = :id";
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql,
                            new MapSqlParameterSource("id", id),
                            this::mapRowToOrder)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Order order) {
        String sql = """
                UPDATE orders SET
                    seat_letter = :seatLetter,
                    seat_number = :seatNumber,
                    status = :status,
                    buyer_email = :buyerEmail,
                    products_qty = :productsQty,
                    total_price = :totalPrice,
                    payment_status = :paymentStatus,
                    payment_date = :paymentDate,
                    payment_gateway = :paymentGateway
                WHERE id = :id
                """;
        int result = jdbcTemplate.update(sql, mapOrderToParams(order));
        if (result == 0) {
            throw new IllegalStateException("Order with ID " + order.getId() + " not found for update.");
        }

    }

    @Override
    public void deleteById(UUID id) {
        jdbcTemplate.update("DELETE FROM orders WHERE id = :id",
                new MapSqlParameterSource("id", id));
    }

    private MapSqlParameterSource mapOrderToParams(Order order) {
        return new MapSqlParameterSource()
                .addValue("id", order.getId())
                .addValue("seatLetter", order.getSeatLetter())
                .addValue("seatNumber", order.getSeatNumber())
                .addValue("status", order.getStatus().name())
                .addValue("buyerEmail", order.getBuyerEmail())
                .addValue("productsQty", serializeProductsQty(order.getProductsQty()))
                .addValue("totalPrice", order.getTotalPrice())
                .addValue("paymentStatus", order.getPaymentStatus())
                .addValue("paymentDate", order.getPaymentDate())
                .addValue("paymentGateway", order.getPaymentGateway());
    }

    private Order mapRowToOrder(ResultSet rs, int rowNum) throws SQLException {
        return new Order(
                UUID.fromString(rs.getString("id")),
                rs.getString("seat_letter"),
                rs.getInt("seat_number"),
                OrderStatus.valueOf(rs.getString("status")),
                rs.getString("buyer_email"),
                deserializeProductIds(rs.getString("products_qty")),
                rs.getDouble("total_price"),
                rs.getString("payment_status"),
                rs.getTimestamp("payment_date") != null
                        ? rs.getTimestamp("payment_date").toLocalDateTime()
                        : null,
                rs.getString("payment_gateway")
        );
    }

    private PGobject serializeProductsQty(Map<UUID, Integer> productsQty) {
        if (productsQty == null || productsQty.isEmpty()) return null;
        try {
            PGobject pgObject = new PGobject();
            pgObject.setType("jsonb");
            pgObject.setValue(objectMapper.writeValueAsString(productsQty));
            return pgObject;
        } catch (Exception e) {
            // Log the error or handle it as needed, but should not throw an exception
            return null;
        }
    }

    private Map<UUID, Integer> deserializeProductIds(String csv) {
        if (csv == null || csv.isEmpty()) return Collections.emptyMap();
        try {
            return objectMapper.readValue(csv, objectMapper.getTypeFactory()
                    .constructMapType(Map.class, UUID.class, Integer.class));
        } catch (Exception e) {
            // Log the error or handle it as needed, but should not throw an exception
            return Collections.emptyMap();
        }
    }
}
