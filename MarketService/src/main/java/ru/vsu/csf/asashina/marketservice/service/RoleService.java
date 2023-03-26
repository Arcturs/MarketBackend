package ru.vsu.csf.asashina.marketservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.csf.asashina.marketservice.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketservice.mapper.RoleMapper;
import ru.vsu.csf.asashina.marketservice.model.dto.RoleDTO;
import ru.vsu.csf.asashina.marketservice.model.entity.Role;
import ru.vsu.csf.asashina.marketservice.repository.RoleRepository;

import static ru.vsu.csf.asashina.marketservice.model.constant.RoleName.ADMIN;
import static ru.vsu.csf.asashina.marketservice.model.constant.RoleName.USER;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public RoleDTO getUserRole() {
        Role role = findRoleByName(USER);
        return roleMapper.toDTOFromEntity(role);
    }

    private Role findRoleByName(String roleName) {
        return roleRepository.findRoleByName(roleName).orElseThrow(
                () -> new ObjectNotExistException("Following role does not exist")
        );
    }

    public RoleDTO getAdminRole() {
        Role role = findRoleByName(ADMIN);
        return roleMapper.toDTOFromEntity(role);
    }
}
