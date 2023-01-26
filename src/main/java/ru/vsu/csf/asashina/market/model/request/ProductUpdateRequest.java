package ru.vsu.csf.asashina.market.model.request;

import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductUpdateRequest {

    private String description;

    @PositiveOrZero
    private Float price;

    @Positive
    private Integer amount;
}
