package ru.vsu.csf.asashina.marketserver.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import ru.vsu.csf.asashina.marketserver.serializer.PriceJsonSerializer;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class OrderProductDTO {

    private Integer amount;

    @JsonSerialize(using = PriceJsonSerializer.class)
    private Float overallPrice;

    private ProductDTO product;
}
