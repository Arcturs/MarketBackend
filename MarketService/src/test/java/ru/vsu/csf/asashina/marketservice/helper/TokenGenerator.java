package ru.vsu.csf.asashina.marketservice.helper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vsu.csf.asashina.marketservice.model.dto.RoleDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketservice.service.TokenService;

import java.util.Set;

import static ru.vsu.csf.asashina.marketservice.model.constant.RoleName.*;

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
                        new RoleDTO(1L, ADMIN),
                        new RoleDTO(2L, USER)
                ))
                .build();
    }

    public String generateUserAccessToken() {
        return tokenService.createTokens(createUser()).getAccessToken();
    }

    private UserDTO createUser() {
        return UserDTO.builder()
                .userId(2L)
                .name("User")
                .surname("User")
                .email("user@com.com")
                .passwordHash("$2a$10$1pCaZ.GgVDGNG9aMsoIE/eLFOxf5mCpUFTnbhhBW0S7VPfyYKWbUG")
                .roles(Set.of(
                        new RoleDTO(2L, USER)
                ))
                .build();
    }
}
