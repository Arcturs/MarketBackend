package ru.vsu.csf.asashina.marketserver.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class OrderWithUserDTO {

    private String orderNumber;
    private Boolean isPaid;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm", timezone = "GMT")
    private Instant created;

    private Set<OrderProductDTO> products;
    private UserWithoutPasswordDTO user;
    private BigDecimal finalPrice;
}
