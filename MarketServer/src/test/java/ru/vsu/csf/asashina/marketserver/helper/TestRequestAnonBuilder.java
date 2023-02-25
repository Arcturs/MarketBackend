package ru.vsu.csf.asashina.marketserver.helper;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

import static ru.vsu.csf.asashina.marketserver.model.constant.RoleName.ANONYMOUS;

@Component(ANONYMOUS)
public class TestRequestAnonBuilder implements TestRequestBuilder{

    @Override
    public HttpEntity<Map<String, Object>> createRequest() {
        return new HttpEntity<>(initHeaders());
    }

    @Override
    public HttpEntity<Map<String, Object>> createRequestWithRequestBody(Map<String, Object> requestBody) {
        return new HttpEntity<>(requestBody, initHeaders());
    }
}
