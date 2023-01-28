package ru.vsu.csf.asashina.marketserver.model.dto;

import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserDTO {

    private Long userId;
    private String name;
    private String surname;
    private String passwordHash;
    private String email;
    private Set<RoleDTO> roles;
}
