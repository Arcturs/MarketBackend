package ru.vsu.csf.asashina.marketservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vsu.csf.asashina.marketservice.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketservice.mapper.RoleMapper;
import ru.vsu.csf.asashina.marketservice.model.dto.RoleDTO;
import ru.vsu.csf.asashina.marketservice.model.entity.Role;
import ru.vsu.csf.asashina.marketservice.repository.RoleRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static ru.vsu.csf.asashina.marketservice.model.constant.RoleName.ADMIN;
import static ru.vsu.csf.asashina.marketservice.model.constant.RoleName.USER;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Spy
    private RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    private Role createUserRole() {
        return new Role(2L, USER);
    }

    private RoleDTO createUserRoleDTO() {
        return new RoleDTO(2L, USER);
    }

    private Role createAdminRole() {
        return new Role(1L, ADMIN);
    }

    private RoleDTO createAdminRoleDTO() {
        return new RoleDTO(1L, ADMIN);
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

    @Test
    void getAdminRoleSuccess() {
        //given
        Role roleFromRepository = createAdminRole();
        RoleDTO expectedRole = createAdminRoleDTO();

        when(roleRepository.findRoleByName(ADMIN)).thenReturn(Optional.of(roleFromRepository));

        //when
        RoleDTO result = roleService.getAdminRole();

        //then
        assertEquals(expectedRole, result);
    }

    @Test
    void getAdminRoleThrowsExceptionWhenAdminRoleDoesNotExist() {
        //given
        when(roleRepository.findRoleByName(ADMIN)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> roleService.getAdminRole()).isInstanceOf(ObjectNotExistException.class);
    }
}