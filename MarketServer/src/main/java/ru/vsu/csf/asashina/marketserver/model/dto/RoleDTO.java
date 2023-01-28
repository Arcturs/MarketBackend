package ru.vsu.csf.asashina.marketserver.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RoleDTO {

    private Long roleId;
    private String name;
}
