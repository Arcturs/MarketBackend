package ru.vsu.csf.asashina.marketservice.model.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class CategoryWithProductsDTO {

    private CategoryDTO category;
    private List<ProductDetailedDTO> products;
    private PagingInfoDTO paging;
}
