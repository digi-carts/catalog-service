package com.digicart.catalog.service;

import com.digicart.catalog.entity.Product;
import com.digicart.catalog.repository.CategoryRepository;
import com.digicart.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, categoryRepository);
    }

    @Test
    void findAllReturnsPageEnvelope() {
        Product p = new Product();
        p.setStoreId("s1");
        p.setName("Mug");
        when(productRepository.findFiltered(eq("s1"), eq(""), eq(Collections.emptySet()), any(PageRequest.class))).thenReturn(List.of(p));
        when(productRepository.countFiltered("s1", "", Collections.emptySet())).thenReturn(1L);

        Map<String, Object> result = productService.findAll("s1", null, null, null, null, 1, 20);
        assertThat(result.get("total")).isEqualTo(1L);
        assertThat(result.get("page")).isEqualTo(1);
        assertThat((List<?>) result.get("products")).hasSize(1);
    }

    @Test
    void stockSummaryCounts() {
        when(productRepository.countByStoreId("s1")).thenReturn(10L);
        when(productRepository.countByStoreIdAndStock("s1", 0)).thenReturn(2L);
        when(productRepository.countLowStock("s1", 5)).thenReturn(3L);
        when(productRepository.findTopLowStock(eq("s1"), eq(5), any(PageRequest.class))).thenReturn(List.of());
        Map<String, Object> summary = productService.stockSummary("s1");
        assertThat(summary.get("total")).isEqualTo(10L);
        assertThat(summary.get("outOfStock")).isEqualTo(2L);
        assertThat(summary.get("lowStock")).isEqualTo(3L);
    }

    @Test
    void findTagsDedupes() {
        Product a = new Product();
        a.setStoreId("s1");
        a.setTags(List.of("red", "sale"));
        Product b = new Product();
        b.setStoreId("s1");
        b.setTags(List.of("sale", "new"));
        when(productRepository.findByStoreId("s1")).thenReturn(List.of(a, b));
        assertThat(productService.findTags("s1")).containsExactly("new", "red", "sale");
    }
}
