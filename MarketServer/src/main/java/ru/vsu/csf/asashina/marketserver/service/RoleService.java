package ru.vsu.csf.asashina.marketserver.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.csf.asashina.marketserver.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketserver.mapper.RoleMapper;
import ru.vsu.csf.asashina.marketserver.model.dto.RoleDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.Role;
import ru.vsu.csf.asashina.marketserver.model.constant.RoleName;
import ru.vsu.csf.asashina.marketserver.repository.RoleRepository;

import static ru.vsu.csf.asashina.marketserver.model.constant.RoleName.USER;

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
}
