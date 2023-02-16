package ru.vsu.csf.asashina.marketserver.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

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
    private BigDecimal price;
    private Integer amount;
    private Set<CategoryDTO> categories;
}
