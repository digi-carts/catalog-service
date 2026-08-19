package com.digicart.catalog.cucumber;

import com.digicart.catalog.service.ProductService;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

public class CatalogStepDefinitions {
    @Autowired
    ProductService productService;

    @Before
    public void stubs() {
        when(productService.findAll(anyString(), isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt()))
                .thenReturn(Map.of("products", List.of(), "total", 0L, "page", 1, "limit", 20));
    }
}
