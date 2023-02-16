package ru.vsu.csf.asashina.marketserver.model.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class OrderProductDTO {

    private Integer amount;

    private BigDecimal overallPrice;

    private ProductDTO product;
}
