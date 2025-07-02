package com.immfly.order.management.platform.adapter.out.persistance;

import com.immfly.order.management.platform.domain.model.Order;
import com.immfly.order.management.platform.domain.model.OrderStatus;
import com.immfly.order.management.platform.domain.port.out.OrderRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcOrderRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Order order) {
        String sql = """
                INSERT INTO orders (
                    id, seat_letter, seat_number, status, buyer_email,
                    product_ids, total_price, payment_status, payment_date, payment_gateway
                ) VALUES (
                    :id, :seatLetter, :seatNumber, :status, :buyerEmail,
                    :productIds, :totalPrice, :paymentStatus, :paymentDate, :paymentGateway
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
                    product_ids = :productIds,
                    total_price = :totalPrice,
                    payment_status = :paymentStatus,
                    payment_date = :paymentDate,
                    payment_gateway = :paymentGateway
                WHERE id = :id
                """;
        jdbcTemplate.update(sql, mapOrderToParams(order));
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
                .addValue("productIds", serializeProductIds(order.getProductIds()))
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
                deserializeProductIds(rs.getString("product_ids")),
                rs.getDouble("total_price"),
                rs.getString("payment_status"),
                rs.getTimestamp("payment_date") != null
                        ? rs.getTimestamp("payment_date").toLocalDateTime()
                        : null,
                rs.getString("payment_gateway")
        );
    }

    private String serializeProductIds(List<UUID> productIds) {
        return productIds != null
                ? String.join(",", productIds.stream().map(UUID::toString).toList())
                : null;
    }

    private List<UUID> deserializeProductIds(String csv) {
        if (csv == null || csv.isBlank()) return new ArrayList<>();
        String[] ids = csv.split(",");
        List<UUID> result = new ArrayList<>();
        for (String id : ids) {
            result.add(UUID.fromString(id.trim()));
        }
        return result;
    }
}
