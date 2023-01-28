package ru.vsu.csf.asashina.marketserver.model.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

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

    private List<Long> categoriesId;
}
