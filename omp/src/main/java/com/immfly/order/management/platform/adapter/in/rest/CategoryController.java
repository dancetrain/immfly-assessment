package com.immfly.order.management.platform.adapter.in.rest;

import com.immfly.order.management.platform.adapter.in.rest.dto.CategoryDTO;
import com.immfly.order.management.platform.domain.model.Category;
import com.immfly.order.management.platform.domain.port.out.CategoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.from(category);
    }
}