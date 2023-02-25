package ru.vsu.csf.asashina.marketserver.helper;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;

import static ru.vsu.csf.asashina.marketserver.model.constant.RoleName.USER;

@Component(USER)
@AllArgsConstructor
public class TestRequestUserBuilder implements TestRequestBuilder{

    private final TokenGenerator tokenGenerator;

    @Override
    public HttpEntity<Map<String, Object>> createRequest() {
        return new HttpEntity<>(initHeadersWithUserToken());
    }

    private HttpHeaders initHeadersWithUserToken() {
        HttpHeaders headers = initHeaders();
        headers.setBearerAuth(tokenGenerator.generateUserAccessToken());
        return headers;
    }

    @Override
    public HttpEntity<Map<String, Object>> createRequestWithRequestBody(Map<String, Object> requestBody) {
        return new HttpEntity<>(requestBody, initHeadersWithUserToken());
    }
}
