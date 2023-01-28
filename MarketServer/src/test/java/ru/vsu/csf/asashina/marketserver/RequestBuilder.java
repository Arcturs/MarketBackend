package ru.vsu.csf.asashina.marketserver;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RequestBuilder {

    private final HttpHeaders headers;

    public RequestBuilder() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    public HttpEntity<Map<String, Object>> createRequestWithRequestBody(Map<String, Object> requestBody) {
        return new HttpEntity<>(requestBody, headers);
    }

    public HttpEntity<Map<String, Object>> createRequest() {
        return new HttpEntity<>(headers);
    }
}
