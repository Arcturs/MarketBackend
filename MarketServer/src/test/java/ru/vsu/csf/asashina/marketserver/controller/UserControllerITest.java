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
import ru.vsu.csf.asashina.marketserver.RequestBuilder;
import ru.vsu.csf.asashina.marketserver.repository.UserRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class UserControllerITest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RequestBuilder requestBuilder;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE user_info ALTER COLUMN user_id RESTART WITH 1");
    }

    @Test
    @Sql(scripts = "db/UserControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void signUpNewUserUsingFormSuccess() {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequestWithRequestBody(Map.of(
                "name", "Rick",
                "surname", "Bay",
                "email", "rickbay@google.com",
                "password", "password",
                "repeatPassword", "password"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/users/sign-up", request, String.class);

        //then
        assertEquals(OK, response.getStatusCode());

        assertEquals(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM user_info WHERE email = 'rickbay@google.com')",
                Boolean.class), true);
    }

    @Test
    @Sql(scripts = "db/UserControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void signUpNewUserUsingFormThrowsExceptionForAlreadyExistingEmail() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequestWithRequestBody(Map.of(
                "name", "Rick",
                "surname", "Bay",
                "email", "em1@jar.com",
                "password", "password",
                "repeatPassword", "password"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/users/sign-up", request, String.class);

        //then
        assertEquals(CONFLICT, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "User with following email already exists"
                }
                """,  response.getBody(), false);
    }

    @Test
    @Sql(scripts = "db/UserControllerITestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void signUpNewUserUsingFormThrowsExceptionWhenPasswordsDoNotMatch() throws JSONException {
        //given
        HttpEntity<Map<String, Object>> request = requestBuilder.createRequestWithRequestBody(Map.of(
                "name", "Rick",
                "surname", "Bay",
                "email", "rickbay@google.com",
                "password", "password",
                "repeatPassword", "hdhdhddhdh"
        ));

        //when
        ResponseEntity<String> response = testRestTemplate.postForEntity("/users/sign-up", request, String.class);

        //then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("""
                {
                    "message": "Passwords do not match"
                }
                """,  response.getBody(), false);
    }
}