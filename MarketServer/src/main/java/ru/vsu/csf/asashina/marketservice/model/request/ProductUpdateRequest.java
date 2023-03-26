package ru.vsu.csf.asashina.marketservice.model.request;

import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductUpdateRequest {

    private String description;

    @Digits(integer = 9, fraction = 2)
    private Float price;

    @Positive
    private Integer amount;
}
