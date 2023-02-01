package ru.vsu.csf.asashina.marketserver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import ru.vsu.csf.asashina.marketserver.exception.ObjectAlreadyExistsException;
import ru.vsu.csf.asashina.marketserver.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketserver.exception.PasswordsDoNotMatchException;
import ru.vsu.csf.asashina.marketserver.exception.WrongCredentialsException;
import ru.vsu.csf.asashina.marketserver.mapper.UserMapper;
import ru.vsu.csf.asashina.marketserver.model.dto.RoleDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.Role;
import ru.vsu.csf.asashina.marketserver.model.entity.User;
import ru.vsu.csf.asashina.marketserver.model.enums.RoleName;
import ru.vsu.csf.asashina.marketserver.model.request.LoginRequest;
import ru.vsu.csf.asashina.marketserver.model.request.UserSignUpRequest;
import ru.vsu.csf.asashina.marketserver.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User createValidUser() {
        return User.builder()
                .userId(1L)
                .name("Name")
                .surname("Sur")
                .email("hh@com.com")
                .passwordHash("$2a$10$1pCaZ.GgVDGNG9aMsoIE/eLFOxf5mCpUFTnbhhBW0S7VPfyYKWbUG")
                .roles(Set.of(new Role(1L, RoleName.USER.getName())))
                .build();
    }

    private UserDTO createValidUserDTO() {
        return UserDTO.builder()
                .userId(1L)
                .name("Name")
                .surname("Sur")
                .email("hh@com.com")
                .passwordHash("$2a$10$1pCaZ.GgVDGNG9aMsoIE/eLFOxf5mCpUFTnbhhBW0S7VPfyYKWbUG")
                .roles(Set.of(new RoleDTO(1L, RoleName.USER.getName())))
                .build();
    }

    @Test
    void signUpNewUserSuccess() {
        //given
        UserSignUpRequest request = UserSignUpRequest.builder()
                .name("Name")
                .surname("Sur")
                .email("hh@com.com")
                .password("password")
                .repeatPassword("password")
                .build();

        RoleDTO roleFromService = new RoleDTO(1L, RoleName.USER.getName());
        User savedUser = createValidUser();
        UserDTO expectedUser = createValidUserDTO();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleService.getUserRole()).thenReturn(roleFromService);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //when
        UserDTO result = userService.signUpNewUser(request);

        //then
        assertEquals(expectedUser, result);
    }

    @Test
    void signUpNewUserThrowsExceptionForAlreadyExistingEmailInDB() {
        //given
        UserSignUpRequest request = UserSignUpRequest.builder()
                .name("Name")
                .surname("Sur")
                .email("hh1@com.com")
                .password("password")
                .repeatPassword("password")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        //when, then
        assertThatThrownBy(() -> userService.signUpNewUser(request)).isInstanceOf(ObjectAlreadyExistsException.class);
    }

    @Test
    void signUpNewUserThrowsExceptionWhenPasswordsDoNotMatch() {
        //given
        UserSignUpRequest request = UserSignUpRequest.builder()
                .name("Name")
                .surname("Sur")
                .email("hh@com.com")
                .password("password")
                .repeatPassword("password12")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        //when, then
        assertThatThrownBy(() -> userService.signUpNewUser(request)).isInstanceOf(PasswordsDoNotMatchException.class);
    }

    @Test
    void checkIfUserHasUserRoleSuccess() {
        //given
        UserDTO user = createValidUserDTO();

        when(roleService.getUserRole()).thenReturn(new RoleDTO(1L, RoleName.USER.getName()));

        //when, then
        assertDoesNotThrow(() -> userService.checkIfUserHasUserRole(user));
    }

    @Test
    void checkIfUserHasUserRoleThrowsExceptionWhenHasNot() {
        //given
        UserDTO user = createValidUserDTO();
        user.setRoles(Collections.emptySet());

        when(roleService.getUserRole()).thenReturn(new RoleDTO(1L, RoleName.USER.getName()));

        //when, then
        assertThatThrownBy(() -> userService.checkIfUserHasUserRole(user)).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getUserByEmailSuccess() {
        //given
        String email = "hh@com.com";

        User userFromRepository = createValidUser();
        UserDTO expectedUser = createValidUserDTO();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userFromRepository));

        //when
        UserDTO result = userService.getUserByEmail(email);

        //then
        assertEquals(expectedUser, result);
    }

    @Test
    void getUserByEmailThrowsExceptionForNotExistingUser() {
        //given
        String email = "hh!@com.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> userService.getUserByEmail(email)).isInstanceOf(ObjectNotExistException.class);
    }
}