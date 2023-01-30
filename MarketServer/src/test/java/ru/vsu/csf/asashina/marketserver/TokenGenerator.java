package ru.vsu.csf.asashina.marketserver;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vsu.csf.asashina.marketserver.model.dto.RoleDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketserver.model.enums.RoleName;
import ru.vsu.csf.asashina.marketserver.service.TokenService;

import java.util.Set;

@Component
@AllArgsConstructor
public class TokenGenerator {

    private final TokenService tokenService;

    public String generateAdminAccessToken() {
        return tokenService.createTokens(createAdmin()).getAccessToken();
    }

    private UserDTO createAdmin() {
        return UserDTO.builder()
                .userId(1L)
                .name("Admin")
                .surname("Admin")
                .email("admin@com.com")
                .passwordHash("$2a$10$1pCaZ.GgVDGNG9aMsoIE/eLFOxf5mCpUFTnbhhBW0S7VPfyYKWbUG")
                .roles(Set.of(
                        new RoleDTO(1L, RoleName.ADMIN.getName()),
                        new RoleDTO(2L, RoleName.USER.getName())
                ))
                .build();
    }
}
