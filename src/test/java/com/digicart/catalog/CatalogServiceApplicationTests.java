package com.digicart.catalog;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@Disabled("Needs DATABASE_URL / JWT_SECRET; covered by WebMvcTest and Cucumber")
@SpringBootTest
@TestPropertySource(properties = {
    "spring.liquibase.enabled=false",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
class CatalogServiceApplicationTests {
    @Test
    void contextLoads() {}
}
