package com.immfly.order.management.platform.adapter.out.persistance;

import com.immfly.order.management.platform.domain.model.Product;
import com.immfly.order.management.platform.domain.port.out.ProductRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbcProductRepository implements ProductRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcProductRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Product product) {
        String sql = """
            INSERT INTO products (
                id, name, price, category_id, image, stock
            ) VALUES (
                :id, :name, :price, :categoryId, :image, :stock
            )
        """;
        jdbcTemplate.update(sql, mapProductToParams(product));
    }

    @Override
    public Optional<Product> findById(UUID id) {
        String sql = "SELECT * FROM products WHERE id = :id";
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            sql,
                            new MapSqlParameterSource("id", id),
                            this::mapRowToProduct
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Product product) {
        String sql = """
            UPDATE products SET
                name = :name,
                price = :price,
                category_id = :categoryId,
                image = :image,
                stock = :stock
            WHERE id = :id
        """;
        jdbcTemplate.update(sql, mapProductToParams(product));
    }

    @Override
    public void deleteById(UUID id) {
        jdbcTemplate.update("DELETE FROM products WHERE id = :id",
                new MapSqlParameterSource("id", id));
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM products";
        return jdbcTemplate.query(sql, this::mapRowToProduct);
    }

    private MapSqlParameterSource mapProductToParams(Product product) {
        return new MapSqlParameterSource()
                .addValue("id", product.id())
                .addValue("name", product.name())
                .addValue("price", product.price())
                .addValue("categoryId", product.categoryId())
                .addValue("image", product.image())
                .addValue("stock", product.stock());
    }

    private Product mapRowToProduct(ResultSet rs, int rowNum) throws SQLException {
        return new Product(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getBigDecimal("price"),
                rs.getString("category_id") != null
                        ? UUID.fromString(rs.getString("category_id"))
                        : null,
                rs.getString("image"),
                rs.getInt("stock")
        );
    }
}