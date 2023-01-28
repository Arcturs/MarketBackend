package ru.vsu.csf.asashina.marketserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PagingInfoDTO {

    private Integer pageNumber;
    private Integer size;
    private Integer totalPages;
}
