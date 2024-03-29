package ru.vsu.csf.asashina.marketservice.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserWithoutPasswordDTO {

    private Long userId;
    private String name;
    private String surname;
    private String email;
}
