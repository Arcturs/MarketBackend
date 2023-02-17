package ru.vsu.csf.asashina.marketserver.helper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface TestRequestBuilder {

    HttpEntity<Map<String, Object>> createRequest();

    HttpEntity<Map<String, Object>> createRequestWithRequestBody(Map<String, Object> requestBody);

    default HttpHeaders initHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
