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
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.vsu.csf.asashina.marketserver.helper.TestRequestBuilder;
import ru.vsu.csf.asashina.marketserver.repository.CategoryRepository;
import ru.vsu.csf.asashina.marketserver.repository.OrderRepository;
import ru.vsu.csf.asashina.marketserver.repository.ProductRepository;
import ru.vsu.csf.asashina.marketserver.repository.UserRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.*;
import static ru.vsu.csf.asashina.marketserver.model.constant.RoleName.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class OrderControllerITest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Map<String, TestRequestBuilder> testRequestBuilderMap;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();

        userRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE user_info ALTER COLUMN user_id RESTART WITH 1");

        productRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE product ALTER COLUMN product_id RESTART WITH 1");

        categoryRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE category ALTER COLUMN category_id RESTART WITH 1");
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllUsersOrdersInPagesWithoutParamsSuccess() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders", GET, request, String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "orders": [
                        {
                            "orderNumber": "57996957-a2cf-4435-a2d3-c6c4fb5d3a6c",
                            "isPaid": true,
                            "created": "14.02.2022 18:30",
                            "products": [
                                {
                                    "amount": 8,
                                    "overallPrice": 800.00,
                                    "product": {
                                        "productId": 2,
                                        "name": "Name 2",
                                        "price": 100.00,
                                        "categories": []
                                    }
                                }
                            ],
                            "finalPrice": 800.00
                        },
                        {
                            "orderNumber": "9e4dca52-c91a-43d7-8abc-426af8730733",
                            "isPaid": false,
                            "created": "15.02.2022 04:05",
                            "products": [
                                {
                                    "amount": 3,
                                    "overallPrice": 300.00,
                                    "product": {
                                        "productId": 2,
                                        "name": "Name 2",
                                        "price": 100.00,
                                        "categories": []
                                    }
                                },
                                {
                                    "amount": 1,
                                    "overallPrice": 56.60,
                                    "product": {
                                        "productId": 3,
                                        "name": "Name 3",
                                        "price": 56.60,
                                        "categories": [
                                            {
                                                "categoryId": 1,
                                                "name":"N"
                                            }
                                        ]
                                    }
                                }
                            ],
                            "finalPrice": 356.60
                        }
                    ],
                    "paging": {
                        "pageNumber": 1,
                        "size": 5,
                        "totalPages": 1
                    }
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllUsersOrdersInPagesWithParamsSuccess() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders?pageNumber=2&size=1&isAsc=", GET, request, String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "orders": [
                        {
                            "orderNumber": "9e4dca52-c91a-43d7-8abc-426af8730733",
                            "isPaid": false,
                            "created": "15.02.2022 04:05",
                            "products": [
                                {
                                    "amount": 3,
                                    "overallPrice": 300.00,
                                    "product": {
                                        "productId": 2,
                                        "name": "Name 2",
                                        "price": 100.00,
                                        "categories": []
                                    }
                                },
                                {
                                    "amount": 1,
                                    "overallPrice": 56.60,
                                    "product": {
                                        "productId": 3,
                                        "name": "Name 3",
                                        "price": 56.60,
                                        "categories": [
                                            {
                                                "categoryId": 1,
                                                "name":"N"
                                            }
                                        ]
                                    }
                                }
                            ],
                            "finalPrice": 356.60
                        }
                    ],
                    "paging": {
                        "pageNumber": 2,
                        "size": 1,
                        "totalPages": 2
                    }
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllUsersOrdersInPagesThrowsExceptionForAnonymousUser() {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS).createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders?size=1", GET, request, String.class);

        //then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllUsersOrdersInPagesThrowsExceptionForInvalidPageNumber() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders?pageNumber=ab", GET, request, String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                   "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'; For input string: \\"ab\\""
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllUsersOrdersInPagesThrowsExceptionForInvalidSize() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders?size=ab", GET, request, String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                   "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'; For input string: \\"ab\\""
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllUsersOrdersInPagesThrowsExceptionForPageOutOfRange() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders?pageNumber=2", GET, request, String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                   "message": "Page number is out of range"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getUsersOrderByIdSuccess() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/9e4dca52-c91a-43d7-8abc-426af8730733",
                GET, request, String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "orderNumber": "9e4dca52-c91a-43d7-8abc-426af8730733",
                    "isPaid": false,
                    "created": "15.02.2022 04:05",
                    "products": [
                        {
                            "amount": 3,
                            "overallPrice": 300.00,
                            "product": {
                                "productId": 2,
                                "name": "Name 2",
                                "price": 100.00,
                                "categories": []
                            }
                        },
                        {
                            "amount": 1,
                            "overallPrice": 56.60,
                            "product": {
                                "productId": 3,
                                "name": "Name 3",
                                "price": 56.60,
                                "categories": [
                                    {
                                        "categoryId": 1,
                                        "name":"N"
                                    }
                                ]
                            }
                        }
                    ],
                    "user": {
                        "userId": 2,
                        "name": "User",
                        "surname": "User",
                        "email": "user@com.com"
                    },
                    "finalPrice": 356.60
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getUsersOrderByIdSuccessAdminsCase() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ADMIN).createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/9e4dca52-c91a-43d7-8abc-426af8730733",
                GET, request, String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "orderNumber": "9e4dca52-c91a-43d7-8abc-426af8730733",
                    "isPaid": false,
                    "created": "15.02.2022 04:05",
                    "products": [
                        {
                            "amount": 3,
                            "overallPrice": 300.00,
                            "product": {
                                "productId": 2,
                                "name": "Name 2",
                                "price": 100.00,
                                "categories": []
                            }
                        },
                        {
                            "amount": 1,
                            "overallPrice": 56.60,
                            "product": {
                                "productId": 3,
                                "name": "Name 3",
                                "price": 56.60,
                                "categories": [
                                    {
                                        "categoryId": 1,
                                        "name":"N"
                                    }
                                ]
                            }
                        }
                    ],
                    "user": {
                        "userId": 2,
                        "name": "User",
                        "surname": "User",
                        "email": "user@com.com"
                    },
                    "finalPrice": 356.60
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getUsersOrderByIdThrowsExceptionForAnonymousRequest() {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS).createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/9e4dca52-c91a-43d7-8abc-426af8730733",
                GET, request, String.class);

        //then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getUsersOrderByIdThrowsExceptionForNonExistingOrder() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/ab", GET, request, String.class);

        //then
        assertEquals(NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Order with following id number does not exist"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getUsersOrderByIdThrowsExceptionWhenUserHasNoAccess() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequest();

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/7da68357-5be1-4953-a4eb-90bfb9b6aebe",
                GET, request, String.class);

        //then
        assertEquals(FORBIDDEN, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Access denied"
                }
                """, response.getBody(), false);
    }
}