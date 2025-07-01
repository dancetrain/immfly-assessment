package com.immfly.order.management.platform.domain.model;


import java.util.UUID;

public class Category {
    private UUID id;
    private String name;
    private UUID parentCategoryId;

    public Category(UUID id, String name, UUID parentCategoryId) {
        this.id = id;
        this.name = name;
        this.parentCategoryId = parentCategoryId;
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public UUID getParentCategoryId() {
        return parentCategoryId;
    }
}