package ru.vsu.csf.asashina.market.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ProductDTO {

    private Long productId;
    private String name;
    private String description;
    private Float price;
    private Integer amount;
}
