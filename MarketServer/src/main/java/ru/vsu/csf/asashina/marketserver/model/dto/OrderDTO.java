package ru.vsu.csf.asashina.marketserver.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import ru.vsu.csf.asashina.marketserver.serializer.PriceJsonSerializer;

import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class OrderDTO {

    private String orderNumber;
    private Boolean isPaid;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm", timezone = "GMT")
    private Instant created;

    private Set<OrderProductDTO> products;

    @JsonSerialize(using = PriceJsonSerializer.class)
    private Double finalPrice;
}
