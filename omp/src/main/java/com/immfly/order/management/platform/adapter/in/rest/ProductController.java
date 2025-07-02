package com.immfly.order.management.platform.adapter.in.rest;

import com.immfly.order.management.platform.adapter.in.rest.dto.ProductDTO;
import com.immfly.order.management.platform.domain.model.Product;
import com.immfly.order.management.platform.domain.port.out.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private ProductDTO toDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategoryId(),
                product.getImage(),
                product.getStock()
        );
    }
}