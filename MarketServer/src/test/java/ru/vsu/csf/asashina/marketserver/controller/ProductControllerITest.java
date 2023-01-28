package ru.vsu.csf.asashina.marketserver.controller;

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
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.vsu.csf.asashina.marketserver.RequestBuilder;
import ru.vsu.csf.asashina.marketserver.repository.CategoryRepository;
import ru.vsu.csf.asashina.marketserver.repository.ProductRepository;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class ProductControllerITest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RequestBuilder requestBuilder;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE product ALTER COLUMN product_id RESTART WITH 1");

        categoryRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE category ALTER COLUMN category_id RESTART WITH 1");
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInPagesWithoutParams() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/products", String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                     {
                         "paging": {
                             "pageNumber": 1,
                             "size": 5,
                             "totalPages": 1
                         },
                         "data": [
                            {
                                "productId": 3,
                                "name": "Name 3",
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
                            },
                            {
                                "productId": 2,
                                "name": "Name 2",
                                "description": null,
                                "price": 100.00,
                                "amount": 12
                            }
                         ]
                     }
                     """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInPagesWithPageAndSizeParams() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/products?pageNumber=2&size=2", String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                     {
                         "paging": {
                             "pageNumber": 2,
                             "size": 2,
                             "totalPages": 2
                         },
                         "data": [
                            {
                                "productId": 2,
                                "name": "Name 2",
                                "description": null,
                                "price": 100.00,
                                "amount": 12
                            }
                         ]
                     }
                     """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInPagesWithAscEqualsFalse() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/products?isAsc=", String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                     {
                         "paging": {
                             "pageNumber": 1,
                             "size": 5,
                             "totalPages": 1
                         },
                         "data": [
                            {
                                "productId": 2,
                                "name": "Name 2",
                                "description": null,
                                "price": 100.00,
                                "amount": 12
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
                            },
                            {
                                "productId": 3,
                                "name": "Name 3",
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
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInPagesWithNameParam() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/products?name=1", String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                     {
                         "paging": {
                             "pageNumber": 1,
                             "size": 5,
                             "totalPages": 1
                         },
                         "data": [
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
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInPagesThrowsExceptionForInvalidPageNumber() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/products?pageNumber=ab", String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                     {
                        "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'; For input string: \\"ab\\""
                     }
                     """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInPagesThrowsExceptionForInvalidSize() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/products?size=ab", String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                     {
                        "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'; For input string: \\"ab\\""
                     }
                     """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllProductsInPagesThrowsExceptionWhenPageOutOfRange() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/products?pageNumber=2&size=3", String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                     {
                        "message": "Page number is out of range"
                     }
                     """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getProductByIdSuccess() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/products/1", String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
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
                """,  response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getProductByIdThrowsExceptionForInvalidId() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/products/ab", String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \\"ab\\""
                }
                """,  response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getProductByIdThrowsExceptionForNonExistingProduct() throws JSONException {
        //when
        ResponseEntity<String> response = testRestTemplate.getForEntity("/products/100", String.class);

        //then
        assertEquals(NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Product with following id does not exist"
                }
                """,  response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createProductSuccess() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequestWithRequestBody(Map.of(
                "name", "Name 4",
                "price", "67.67",
                "amount", "4"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/products", request, String.class);

        //then
        assertEquals(CREATED, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "productId": 4,
                    "name": "Name 4",
                    "description": null,
                    "price": 67.67,
                    "amount": 4
                }
                """,  response.getBody(), false);

        assertEquals(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM product WHERE product_id = 4)",
                Boolean.class), true);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createProductSuccessWithCategory() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequestWithRequestBody(Map.of(
                "name", "Name 4",
                "price", "67.67",
                "amount", "4",
                "categoriesId", List.of(2L)
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/products", request, String.class);

        //then
        assertEquals(CREATED, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "productId": 4,
                    "name": "Name 4",
                    "description": null,
                    "price": 67.67,
                    "amount": 4,
                    "categories": [
                        {
                            "categoryId": 2,
                            "name": "V"
                        }
                    ]
                }
                """,  response.getBody(), false);

        assertEquals(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM product WHERE product_id = 4)",
                Boolean.class), true);
        assertEquals(jdbcTemplate.queryForObject("SELECT category_id FROM product_category WHERE product_id = 4",
                        Long.class), 2L);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createProductThrowsExceptionWhenObjectWithRequestedNameAlreadyExists() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequestWithRequestBody(Map.of(
                "name", "Name 1",
                "price", "67.67",
                "amount", "4"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/products", request, String.class);

        //then
        assertEquals(CONFLICT, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Product with following name already exists"
                }
                """,  response.getBody(), false);

        assertEquals(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM product WHERE product_id = 4)",
                Boolean.class), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateProductByIdSuccess() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequestWithRequestBody(Map.of(
                "amount", "12",
                "description", "Cool"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/products/1", HttpMethod.PUT, request,
                String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "productId": 1,
                    "name": "Name 1",
                    "description": "Cool",
                    "price": 100.00,
                    "amount": 12,
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
                """, response.getBody(), false);

        assertEquals(jdbcTemplate.queryForMap("SELECT description, amount FROM product WHERE product_id = 1"),
                Map.of(
                        "description", "Cool",
                        "amount", 12
                ));
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateProductByIdThrowsExceptionForInvalidId() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequestWithRequestBody(Map.of(
                "amount", "12",
                "description", "Cool"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/products/ab", HttpMethod.PUT, request,
                String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \\"ab\\""
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateProductByIdThrowsExceptionForNotExistingObject() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequestWithRequestBody(Map.of(
                "amount", "12",
                "description", "Cool"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/products/4", HttpMethod.PUT, request,
                String.class);

        //then
        assertEquals(NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Product with following id does not exist"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteProductByIdSuccess() {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/products/1", HttpMethod.DELETE, request,
                String.class);

        //then
        assertEquals(NO_CONTENT, response.getStatusCode());

        assertEquals(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM product WHERE product_id = 1)",
                Boolean.class), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteProductByIdThrowsExceptionForInvalidId() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/products/ab", HttpMethod.DELETE, request,
                String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \\"ab\\""
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteProductByIdThrowsExceptionForNotExistingId() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/products/100", HttpMethod.DELETE, request,
                String.class);

        //then
        assertEquals(NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Product with following id does not exist"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void removeCategoryFromProductSuccess() {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/products/1/remove-category/1",
                HttpMethod.DELETE, request, String.class);

        //then
        assertEquals(NO_CONTENT, response.getStatusCode());

        assertEquals(jdbcTemplate.queryForObject("SELECT category_id FROM product_category WHERE product_id = 1",
                Long.class), 2L);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void removeCategoryFromProductThrowsExceptionForInvalidProductId() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/products/ab/remove-category/1",
                HttpMethod.DELETE, request, String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \\"ab\\""
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void removeCategoryFromProductThrowsExceptionForInvalidCategoryId() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/products/1/remove-category/ab",
                HttpMethod.DELETE, request, String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \\"ab\\""
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void removeCategoryFromProductThrowsExceptionForNotExistingProductId() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/products/100/remove-category/1",
                HttpMethod.DELETE, request, String.class);

        //then
        assertEquals(NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Product with following id does not exist"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/ProductControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void removeCategoryFromProductThrowsExceptionForNotExistingCategoryId() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/products/1/remove-category/100",
                HttpMethod.DELETE, request, String.class);

        //then
        assertEquals(NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Category with following id does not exist"
                }
                """, response.getBody(), false);
    }
}