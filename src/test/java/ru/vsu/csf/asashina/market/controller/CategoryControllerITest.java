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
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.vsu.csf.asashina.market.RequestBuilder;
import ru.vsu.csf.asashina.market.repository.CategoryRepository;
import ru.vsu.csf.asashina.market.repository.ProductRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

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
    private RequestBuilder requestBuilder;

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

    @Test
    @Sql(scripts = "db/CategoryControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInCategoryInPagesByIdWithoutParams() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/categories/1/products", String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                   "category": {
                       "categoryId": 1,
                       "name": "N"
                   },
                   "paging": {
                       "pageNumber": 1,
                       "size": 5,
                       "totalPages": 1
                   },
                   "products": [
                      {
                          "productId": 2,
                          "name": "Name 2",
                          "description": null,
                          "price": 56.60,
                          "amount": 3,
                          "categories": [
                              {
                                  "categoryId": 1,
                                  "name": "N"
                              }
                          ]
                      },
                      {
                          "productId": 1,
                          "name": "Name 1",
                          "description": null,
                          "price": 100.00,
                          "amount": 10,
                          "categories": [
                              {
                                  "categoryId": 1,
                                  "name": "N"
                              },
                              {
                                  "categoryId": 2,
                                  "name": "V"
                              }
                          ]
                      }
                   ]
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/CategoryControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInCategoryInPagesByIdWithPageAndSizeParams() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/categories/1/products?pageNumber=2&size=1",
                String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                   "category": {
                       "categoryId": 1,
                       "name": "N"
                   },
                   "paging": {
                       "pageNumber": 2,
                       "size": 1,
                       "totalPages": 2
                   },
                   "products": [
                      {
                          "productId": 1,
                          "name": "Name 1",
                          "description": null,
                          "price": 100.00,
                          "amount": 10,
                          "categories": [
                              {
                                  "categoryId": 1,
                                  "name": "N"
                              },
                              {
                                  "categoryId": 2,
                                  "name": "V"
                              }
                          ]
                      }
                   ]
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/CategoryControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInCategoryInPagesByIdWithAscEqualsFalse() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/categories/1/products?isAsc=", String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                   "category": {
                       "categoryId": 1,
                       "name": "N"
                   },
                   "paging": {
                       "pageNumber": 1,
                       "size": 5,
                       "totalPages": 1
                   },
                   "products": [
                      {
                          "productId": 1,
                          "name": "Name 1",
                          "description": null,
                          "price": 100.00,
                          "amount": 10,
                          "categories": [
                              {
                                  "categoryId": 1,
                                  "name": "N"
                              },
                              {
                                  "categoryId": 2,
                                  "name": "V"
                              }
                          ]
                      },
                      {
                          "productId": 2,
                          "name": "Name 2",
                          "description": null,
                          "price": 56.60,
                          "amount": 3,
                          "categories": [
                              {
                                  "categoryId": 1,
                                  "name": "N"
                              }
                          ]
                      }
                   ]
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/CategoryControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInCategoryInPagesByIdWithNameParam() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/categories/1/products?name=1",
                String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                   "category": {
                       "categoryId": 1,
                       "name": "N"
                   },
                   "paging": {
                       "pageNumber": 1,
                       "size": 5,
                       "totalPages": 1
                   },
                   "products": [
                      {
                          "productId": 1,
                          "name": "Name 1",
                          "description": null,
                          "price": 100.00,
                          "amount": 10,
                          "categories": [
                              {
                                  "categoryId": 1,
                                  "name": "N"
                              },
                              {
                                  "categoryId": 2,
                                  "name": "V"
                              }
                          ]
                      }
                   ]
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/CategoryControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInCategoryInPagesByIdThrowsExceptionForInvalidId() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/categories/ab/products", String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                   "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \\"ab\\""
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/CategoryControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInCategoryInPagesByIdThrowsExceptionForNotExistingId() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/categories/100/products", String.class);

        //then
        assertEquals(NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                   "message": "Category with following id does not exist"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/CategoryControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInCategoryInPagesByIdThrowsExceptionForInvalidPageNumber() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/categories/1/products?pageNumber=ab",
                String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                   "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'; For input string: \\"ab\\""
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/CategoryControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInCategoryInPagesByIdThrowsExceptionForInvalidSize() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/categories/1/products?size=ab",
                String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                   "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'; For input string: \\"ab\\""
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/CategoryControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInCategoryInPagesByIdThrowsExceptionWhenPageOutOfRange() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/categories/1/products?pageNumber=2&size=3",
                String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                   "message": "Page number is out of range"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/CategoryControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createCategoryFromRequestSuccess() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequestWithRequestBody(Map.of(
                "name", "R"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/categories", request, String.class);

        //then
        assertEquals(CREATED, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "categoryId": 3,
                    "name": "R"
                }
                """, response.getBody(), false);

        assertEquals(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM category WHERE category_id = 3)",
                Boolean.class), true);
    }

    @Test
    @Sql(scripts = "db/CategoryControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createCategoryFromRequestThrowsExceptionForAlreadyExistingCategory() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequestWithRequestBody(Map.of(
                "name", "n"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/categories", request, String.class);

        //then
        assertEquals(CONFLICT, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Category with following name already exists"
                }
                """, response.getBody(), false);
    }
}