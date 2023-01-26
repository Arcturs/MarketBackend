package ru.vsu.csf.asashina.market.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import ru.vsu.csf.asashina.market.serializer.PriceJsonSerializer;

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

    @JsonSerialize(using = PriceJsonSerializer.class)
    private Float price;

    private Integer amount;
}
