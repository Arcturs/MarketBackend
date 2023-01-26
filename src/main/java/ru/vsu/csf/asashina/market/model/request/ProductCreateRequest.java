package ru.vsu.csf.asashina.market.model.request;

import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductCreateRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;

    @NotNull
    @PositiveOrZero
    private Float price;

    @NotNull
    @Positive
    private Integer amount;
}