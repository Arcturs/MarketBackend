package ru.vsu.csf.asashina.marketserver.model.dto;

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
    private List<ProductDTO> products;
    private PagingInfoDTO paging;
}
