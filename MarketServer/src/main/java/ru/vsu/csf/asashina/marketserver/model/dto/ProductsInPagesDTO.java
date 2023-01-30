package ru.vsu.csf.asashina.marketserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductsInPagesDTO {

    private List<ProductDTO> products;
    private PagingInfoDTO paging;
}
