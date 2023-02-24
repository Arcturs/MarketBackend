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
import ru.vsu.csf.asashina.marketserver.repository.RefreshTokenRepository;
import ru.vsu.csf.asashina.marketserver.repository.RoleRepository;
import ru.vsu.csf.asashina.marketserver.repository.UserRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;
import static ru.vsu.csf.asashina.marketserver.model.constant.RoleName.ANONYMOUS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class AuthControllerITest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Map<String, TestRequestBuilder> testRequestBuilderMap;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE user_info ALTER COLUMN user_id RESTART WITH 1");

        roleRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE role ALTER COLUMN role_id RESTART WITH 1");

        refreshTokenRepository.deleteAll();
    }

    @Test
    @Sql(scripts = "db/AuthControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void signUpNewUserUsingFormSuccess() {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS)
                .createRequestWithRequestBody(Map.of(
                        "name", "Rick",
                        "surname", "Bay",
                        "email", "rickbay@google.com",
                        "password", "password",
                        "repeatPassword", "password"
                ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/sign-up", request, String.class);

        //then
        assertEquals(OK, response.getStatusCode());

        assertEquals(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM user_info WHERE email = 'rickbay@google.com')",
                Boolean.class), true);
        assertEquals(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM refresh_token WHERE user_id = 3)",
                Boolean.class), true);
    }

    @Test
    @Sql(scripts = "db/AuthControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void signUpNewUserUsingFormThrowsExceptionForAlreadyExistingEmail() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS)
                .createRequestWithRequestBody(Map.of(
                        "name", "Rick",
                        "surname", "Bay",
                        "email", "em1@jar.com",
                        "password", "password",
                        "repeatPassword", "password"
                ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/sign-up", request, String.class);

        //then
        assertEquals(CONFLICT, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "User with following email already exists"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/AuthControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void signUpNewUserUsingFormThrowsExceptionWhenPasswordsDoNotMatch() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS)
                .createRequestWithRequestBody(Map.of(
                        "name", "Rick",
                        "surname", "Bay",
                        "email", "rickbay@google.com",
                        "password", "password",
                        "repeatPassword", "hdhdhddhdh"
                ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/sign-up", request, String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Passwords do not match"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/AuthControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void loginUserSuccess() {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS)
                .createRequestWithRequestBody(Map.of(
                        "email", "em1@jar.com",
                        "password", "password"
                ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/login", request, String.class);

        //then
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    @Sql(scripts = "db/AuthControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void loginUserThrowsExceptionForNonExistingEmail() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS)
                .createRequestWithRequestBody(Map.of(
                        "email", "em12@jar.com",
                        "password", "password"
                ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/login", request, String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Wrong email or password"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/AuthControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void loginUserThrowsExceptionWhenPasswordDoesNotMatch() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS)
                .createRequestWithRequestBody(Map.of(
                        "email", "em1@jar.com",
                        "password", "dumbpass"
                ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/login", request, String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Wrong email or password"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/AuthControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void loginUserThrowsExceptionWhenUserDoesNotHaveRole() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS)
                .createRequestWithRequestBody(Map.of(
                        "email", "norole@norole.com",
                        "password", "password"
                ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/login", request, String.class);

        //then
        assertEquals(FORBIDDEN, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Access denied"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/AuthControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void refreshTokenSuccess() {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS)
                .createRequestWithRequestBody(Map.of(
                        "refreshToken", "8e58d2a2-8ba3-4afd-9d8b-d68a15a0dec4"
                ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/refresh-token", request,
                String.class);

        //then
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    @Sql(scripts = "db/AuthControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void refreshTokenThrowsExceptionForNonExistingRefreshToken() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS)
                .createRequestWithRequestBody(Map.of(
                        "refreshToken", "8e58d2a5-8ba3-4afd-9d8b-d68a15a0dec4"
                ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/refresh-token", request,
                String.class);

        //then
        assertEquals(NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Following refresh token does not exist"
                }
                """, response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/AuthControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void refreshTokenThrowsExceptionForExpiredToken() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = testRequestBuilderMap.get(ANONYMOUS)
                .createRequestWithRequestBody(Map.of(
                        "refreshToken", "08d171a8-1895-4340-a88f-1536fd8315b0"
                ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/refresh-token", request,
                String.class);

        //then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Token is already expired"
                }
                """, response.getBody(), false);
    }
}