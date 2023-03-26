package ru.vsu.csf.asashina.marketservice.model.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RoleDTO implements GrantedAuthority {

    private Long roleId;
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
