package ru.vsu.csf.asashina.market.controller;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.vsu.csf.asashina.market.repository.CategoryRepository;
import ru.vsu.csf.asashina.market.repository.ProductRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class CategoryControllerITest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE category ALTER COLUMN category_id RESTART WITH 1");

        productRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE product ALTER COLUMN product_id RESTART WITH 1");
    }

    @Test
    @Sql(scripts = "db/CategoryControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllCategoriesInListSuccess() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/categories", String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                [
                    {
                        "categoryId": 1,
                        "name": "N"
                    },
                    {
                        "categoryId": 2,
                        "name": "V"
                    }
                ]
                """, response.getBody(), false);
    }
}