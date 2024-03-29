package ru.vsu.csf.asashina.marketservice.model;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.vsu.csf.asashina.marketservice.model.dto.ExceptionDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.PagingInfoDTO;

import java.util.List;
import java.util.Map;

public class ResponseBuilder {

    public static ResponseEntity<?> build(HttpStatus httpStatus, Map<String, Object> data) {
        return ResponseEntity.status(httpStatus).body(data);
    }

    public static ResponseEntity<?> build(HttpStatus httpStatus, Object data) {
        return ResponseEntity.status(httpStatus).body(data);
    }

    public static ResponseEntity<?> build(Page<?> data, Integer pageNumber, Integer size) {
        return build(new PagingInfoDTO(pageNumber, size, data.getTotalPages()),
                data.getContent());
    }

    public static ResponseEntity<?> build(PagingInfoDTO pagingInfoDTO, List<?> data) {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "paging", pagingInfoDTO,
                "data", data
        ));
    }

    public static ResponseEntity<?> build(HttpStatus httpStatus, Exception e) {
        return build(httpStatus, new ExceptionDTO(e.getMessage()));
    }

    public static ResponseEntity<?> buildWithoutBodyResponse(HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).build();
    }
}
