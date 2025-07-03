package com.immfly.order.management.platform.adapter.in.rest;

import com.immfly.order.management.platform.adapter.in.rest.dto.CategoryDTO;
import com.immfly.order.management.platform.adapter.in.rest.dto.CategoryRequest;
import com.immfly.order.management.platform.domain.model.Category;
import com.immfly.order.management.platform.domain.port.out.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryManagementController {
    private final CategoryRepository categoryRepository;

    public CategoryManagementController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryRequest request) {
        UUID newId = UUID.randomUUID();
        Category category = new Category(newId, request.getName(), request.getParentCategoryId());
        categoryRepository.save(category);
        return ResponseEntity.ok(toDTO(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable UUID id,
            @RequestBody CategoryRequest request) {
        Category updated = new Category(id, request.getName(), request.getParentCategoryId());
        categoryRepository.update(updated);
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.from(category);
    }
}
