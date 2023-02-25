package ru.vsu.csf.asashina.marketserver.helper;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import ru.vsu.csf.asashina.marketserver.model.constant.RoleName;

import java.util.Map;

import static ru.vsu.csf.asashina.marketserver.model.constant.RoleName.ADMIN;

@Component(ADMIN)
@AllArgsConstructor
public class TestRequestAdminBuilder implements TestRequestBuilder{

    private final TokenGenerator tokenGenerator;

    @Override
    public HttpEntity<Map<String, Object>> createRequest() {
        return new HttpEntity<>(initHeadersWithAdminToken());
    }

    private HttpHeaders initHeadersWithAdminToken() {
        HttpHeaders headers = initHeaders();
        headers.setBearerAuth(tokenGenerator.generateAdminAccessToken());
        return headers;
    }

    @Override
    public HttpEntity<Map<String, Object>> createRequestWithRequestBody(Map<String, Object> requestBody) {
        return new HttpEntity<>(requestBody, initHeadersWithAdminToken());
    }
}
