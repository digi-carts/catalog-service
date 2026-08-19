package com.digicart.catalog.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.digicart.catalog.exception.GlobalExceptionHandler;
import com.digicart.catalog.controller.HealthController;
import com.digicart.catalog.controller.ProductController;
import com.digicart.catalog.controller.CategoryController;
import com.digicart.catalog.service.ProductService;
import com.digicart.catalog.service.CategoryService;

@CucumberContextConfiguration
@WebMvcTest(controllers = { HealthController.class, ProductController.class, CategoryController.class })
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class CucumberSpringConfiguration {
    @MockBean
    ProductService productService;

    @MockBean
    CategoryService categoryService;

}
