package com.immfly.order.management.platform.adapter.out.persistance;

import com.immfly.order.management.platform.domain.model.Category;
import com.immfly.order.management.platform.domain.port.out.CategoryRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcCategoryRepository implements CategoryRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcCategoryRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Category category) {
        String sql = """
            INSERT INTO categories (id, name, parent_category_id)
            VALUES (:id, :name, :parentCategoryId)
        """;
        jdbcTemplate.update(sql, mapCategoryToParams(category));
    }

    @Override
    public Optional<Category> findById(UUID id) {
        String sql = "SELECT * FROM categories WHERE id = :id";
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            sql,
                            new MapSqlParameterSource("id", id),
                            this::mapRowToCategory
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Category category) {
        String sql = """
            UPDATE categories SET
                name = :name,
                parent_category_id = :parentCategoryId
            WHERE id = :id
        """;
        jdbcTemplate.update(sql, mapCategoryToParams(category));
    }

    @Override
    public void deleteById(UUID id) {
        jdbcTemplate.update("DELETE FROM categories WHERE id = :id",
                new MapSqlParameterSource("id", id));
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories";
        return jdbcTemplate.query(sql, this::mapRowToCategory);
    }

    private MapSqlParameterSource mapCategoryToParams(Category category) {
        return new MapSqlParameterSource()
                .addValue("id", category.getId())
                .addValue("name", category.getName())
                .addValue("parentCategoryId", category.getParentCategoryId());
    }

    private Category mapRowToCategory(ResultSet rs, int rowNum) throws SQLException {
        return new Category(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getString("parent_category_id") != null
                        ? UUID.fromString(rs.getString("parent_category_id"))
                        : null
        );
    }
}
