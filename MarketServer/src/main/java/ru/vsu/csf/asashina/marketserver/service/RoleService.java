package ru.vsu.csf.asashina.marketserver.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.csf.asashina.marketserver.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketserver.mapper.RoleMapper;
import ru.vsu.csf.asashina.marketserver.model.dto.RoleDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.Role;
import ru.vsu.csf.asashina.marketserver.model.enums.RoleName;
import ru.vsu.csf.asashina.marketserver.repository.RoleRepository;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public RoleDTO getUserRole() {
        Role role = findRoleByName(RoleName.USER);
        return roleMapper.toDTOFromEntity(role);
    }

    private Role findRoleByName(RoleName roleName) {
        return roleRepository.findRoleByName(roleName.getName()).orElseThrow(
                () -> new ObjectNotExistException("Following role does not exist")
        );
    }
}
