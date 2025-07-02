package com.immfly.order.management.platform.adapter.in.rest;

import com.immfly.order.management.platform.ContainerPostgresSQLTest;
import com.immfly.order.management.platform.OrderManagementSystemTest;
import com.immfly.order.management.platform.domain.model.Category;
import com.immfly.order.management.platform.domain.port.out.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@OrderManagementSystemTest
@AutoConfigureMockMvc
class CategoryControllerTest extends ContainerPostgresSQLTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    private UUID savedCategoryId;

    @BeforeEach
    void setUp() {
        // Clean up previous test data if needed
        categoryRepository.findAll().forEach(cat -> categoryRepository.deleteById(cat.getId()));

        // Insert test category
        savedCategoryId = UUID.randomUUID();
        Category category = new Category(savedCategoryId, "Food", null);
        categoryRepository.save(category);
    }

    @Test
    void shouldReturnListOfCategories() throws Exception {
        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedCategoryId.toString()))
                .andExpect(jsonPath("$[0].name").value("Food"))
                .andExpect(jsonPath("$[0].parentCategoryId").doesNotExist());
    }
}