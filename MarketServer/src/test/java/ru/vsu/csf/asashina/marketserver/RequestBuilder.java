package ru.vsu.csf.asashina.marketserver;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class RequestBuilder {

    private final TokenGenerator tokenGenerator;

    public HttpEntity<Map<String, Object>> createRequest() {
        return new HttpEntity<>(initHeaders());
    }

    private HttpHeaders initHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public HttpEntity<Map<String, Object>> createRequestWithAdminToken() {
        return new HttpEntity<>(initHeadersWithAdminToken());
    }

    private HttpHeaders initHeadersWithAdminToken() {
        HttpHeaders headers = initHeaders();
        headers.setBearerAuth(tokenGenerator.generateAdminAccessToken());
        return headers;
    }

    public HttpEntity<Map<String, Object>> createRequestWithRequestBody(Map<String, Object> requestBody) {
        return new HttpEntity<>(requestBody, initHeaders());
    }

    public HttpEntity<Map<String, Object>> createRequestWithRequestBodyAndAdminToken(Map<String, Object> requestBody) {
        return new HttpEntity<>(requestBody, initHeadersWithAdminToken());
    }
}
