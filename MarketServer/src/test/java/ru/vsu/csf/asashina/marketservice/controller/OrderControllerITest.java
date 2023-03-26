package ru.vsu.csf.asashina.marketservice.controller;

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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.vsu.csf.asashina.marketservice.helper.TestRequestBuilder;
import ru.vsu.csf.asashina.marketservice.repository.*;
import ru.vsu.csf.asashina.marketservice.util.UuidUtil;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.*;
import static ru.vsu.csf.asashina.marketservice.model.constant.RoleName.*;

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

    @Autowired
    private RoleRepository roleRepository;

    @SpyBean
    private UuidUtil uuidUtil;

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();

        productRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE product ALTER COLUMN product_id RESTART WITH 1");

        categoryRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE category ALTER COLUMN category_id RESTART WITH 1");

        userRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE user_info ALTER COLUMN user_id RESTART WITH 1");

        roleRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE role ALTER COLUMN role_id RESTART WITH 1");
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

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void manageOrderSuccessWhenOrderAlreadyExistsAndProductInOrder() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequestWithRequestBody(Map.of(
                "productId", "2",
                "amount", "4"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/manage", POST, request, String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "orderNumber": "9e4dca52-c91a-43d7-8abc-426af8730733"
                }
                """, response.getBody(), false);

        assertEquals(4, jdbcTemplate.queryForObject("""
                                                    SELECT amount
                                                    FROM order_product
                                                    WHERE order_number = '9e4dca52-c91a-43d7-8abc-426af8730733'
                                                      AND product_id = 2""", Integer.class));
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void manageOrderSuccessWhenOrderAlreadyExistsAndProductRemovedFromOrder() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequestWithRequestBody(Map.of(
                "productId", "2",
                "amount", "0"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/manage", POST, request, String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "orderNumber": "9e4dca52-c91a-43d7-8abc-426af8730733"
                }
                """, response.getBody(), false);

        assertNotEquals(Boolean.TRUE, jdbcTemplate.queryForObject("""
                SELECT EXISTS(
                  SELECT 1 
                  FROM order_product 
                  WHERE order_number = '9e4dca52-c91a-43d7-8abc-426af8730733'
                    AND product_id = 2)""", Boolean.class));
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void manageOrderSuccessWhenOrderAlreadyExistsAndNewProductAdded() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequestWithRequestBody(Map.of(
                "productId", "1",
                "amount", "2"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/manage", POST, request, String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "orderNumber": "9e4dca52-c91a-43d7-8abc-426af8730733"
                }
                """, response.getBody(), false);

        assertEquals(2, jdbcTemplate.queryForObject("""
                                                    SELECT amount
                                                    FROM order_product
                                                    WHERE order_number = '9e4dca52-c91a-43d7-8abc-426af8730733'
                                                      AND product_id = 1""", Integer.class));
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void manageOrderSuccessWhenNewOrder() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequestWithRequestBody(Map.of(
                "productId", "1",
                "amount", "2"
        ));

        orderRepository.deleteById("9e4dca52-c91a-43d7-8abc-426af8730733"); //deleting not paid order for test purposes
        when(uuidUtil.generateRandomUUIDInString()).thenReturn("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/manage", POST, request, String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "orderNumber": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
                }
                """, response.getBody(), false);

        assertEquals("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", jdbcTemplate.queryForObject("""
                                                    SELECT order_number
                                                    FROM order_info
                                                    WHERE is_paid = false AND user_id = 2""", String.class));
        assertEquals(2, jdbcTemplate.queryForObject("""
                                                    SELECT amount
                                                    FROM order_product
                                                    WHERE order_number = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
                                                      AND product_id = 1""", Integer.class));
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void manageOrderThrowsExceptionWhenUserAnonymous() {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS).createRequestWithRequestBody(Map.of(
                "productId", "2",
                "amount", "4"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/manage", POST, request, String.class);

        //then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void manageOrderThrowsExceptionWhenProductDoesNotExist() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequestWithRequestBody(Map.of(
                "productId", "10",
                "amount", "4"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/manage", POST, request, String.class);

        //then
        assertEquals(NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Product with following id does not exist"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void manageOrderThrowsExceptionWhenUserTriesToAddMoreProductThanInDB() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequestWithRequestBody(Map.of(
                "productId", "2",
                "amount", "40"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/manage", POST, request, String.class);

        //then
        assertEquals(METHOD_NOT_ALLOWED, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "You cannot buy more than 12 products"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void manageOrderThrowsExceptionWhenUserTriesToAddZeroNewProduct() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequestWithRequestBody(Map.of(
                "productId", "1",
                "amount", "0"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/manage", POST, request, String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "You cannot add 0 products"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void payForOrderSuccess() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequestWithRequestBody(Map.of(
                "balance", "500.00"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/pay", POST, request, String.class);

        //then
        assertEquals(OK, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "orderNumber": "9e4dca52-c91a-43d7-8abc-426af8730733"
                }""", response.getBody(), false);

        assertEquals(Boolean.TRUE, jdbcTemplate.queryForObject("""
                        SELECT is_paid
                        FROM order_info
                        WHERE order_number = '9e4dca52-c91a-43d7-8abc-426af8730733'""",
                Boolean.class));
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void payForOrderThrowsExceptionForAnonymous() {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS).createRequestWithRequestBody(Map.of(
                "balance", "500.00"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/pay", POST, request, String.class);

        //then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void payForOrderThrowsExceptionWhenUserHasNoNotPaidOrders() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequestWithRequestBody(Map.of(
                "balance", "500.00"
        ));

        orderRepository.deleteById("9e4dca52-c91a-43d7-8abc-426af8730733"); //deleting not paid order for test purposes

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/pay", POST, request, String.class);

        //then
        assertEquals(METHOD_NOT_ALLOWED, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "All orders are already paid"
                }""", response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void payForOrderThrowsExceptionWhenUserHasNotEnoughMoney() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequestWithRequestBody(Map.of(
                "balance", "0.00"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/pay", POST, request, String.class);

        //then
        assertEquals(METHOD_NOT_ALLOWED, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Low balance"
                }""", response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void payForOrderThrowsExceptionWhenOrderIsEmpty() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequestWithRequestBody(Map.of(
                "balance", "500.00"
        ));

        //deleting products for testing purposes
        jdbcTemplate.execute("DELETE FROM order_product WHERE order_number = '9e4dca52-c91a-43d7-8abc-426af8730733'");

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/pay", POST, request, String.class);

        //then
        assertEquals(METHOD_NOT_ALLOWED, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Order is empty"
                }""", response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/OrderControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void payForOrderThrowsExceptionWhenProductAmountInOrderLargerThanInDB() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(USER).createRequestWithRequestBody(Map.of(
                "balance", "500.00"
        ));

        //changing product amount for testing purposes
        jdbcTemplate.execute("UPDATE product SET amount = 1 WHERE product_id = 2");

        //when
        ResponseEntity<String> response = testRestTemplate.exchange("/orders/pay", POST, request, String.class);

        //then
        assertEquals(METHOD_NOT_ALLOWED, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "You cannot buy more than 1 Name 2"
                }""", response.getBody(), false);
    }
}