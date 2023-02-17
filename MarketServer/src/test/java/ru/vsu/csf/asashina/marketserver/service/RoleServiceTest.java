package ru.vsu.csf.asashina.marketserver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vsu.csf.asashina.marketserver.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketserver.mapper.RoleMapper;
import ru.vsu.csf.asashina.marketserver.model.dto.RoleDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.Role;
import ru.vsu.csf.asashina.marketserver.model.constant.RoleName;
import ru.vsu.csf.asashina.marketserver.repository.RoleRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static ru.vsu.csf.asashina.marketserver.model.constant.RoleName.USER;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Spy
    private RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    private Role createUserRole() {
        return new Role(1L, USER);
    }

    private RoleDTO createUserRoleDTO() {
        return new RoleDTO(1L, USER);
    }

    @Test
    void getUserRoleSuccess() {
        //given
        Role roleFromRepository = createUserRole();
        RoleDTO expectedRole = createUserRoleDTO();

        when(roleRepository.findRoleByName(USER)).thenReturn(Optional.of(roleFromRepository));

        //when
        RoleDTO result = roleService.getUserRole();

        //then
        assertEquals(expectedRole, result);
    }

    @Test
    void getUserRoleThrowsExceptionWhenUserRoleDoesNotExist() {
        //given

        when(roleRepository.findRoleByName(USER)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> roleService.getUserRole()).isInstanceOf(ObjectNotExistException.class);
    }
}