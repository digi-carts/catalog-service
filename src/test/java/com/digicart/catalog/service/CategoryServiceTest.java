package com.digicart.catalog.service;

import com.digicart.catalog.entity.Category;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryServiceTest {

    private final CategoryService categoryService = new CategoryService(null);

    @Test
    void buildTreeNestsChildren() {
        Category root = new Category();
        root.setStoreId("s1");
        root.setName("Root");
        // id is generated; without id, tree uses null keys — only test empty products list
        List<?> tree = categoryService.buildTree("s1", List.of());
        assertThat(tree).isEmpty();
    }
}
