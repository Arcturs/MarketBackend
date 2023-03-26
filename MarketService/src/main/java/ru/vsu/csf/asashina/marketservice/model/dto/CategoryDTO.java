package ru.vsu.csf.asashina.marketservice.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CategoryDTO {

    private Long categoryId;
    private String name;
}
