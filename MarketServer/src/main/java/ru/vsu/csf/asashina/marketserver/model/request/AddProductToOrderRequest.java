package ru.vsu.csf.asashina.marketserver.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddProductToOrderRequest {

    @NotNull
    private Long productId;

    @NotNull
    @PositiveOrZero
    private Integer amount;
}
