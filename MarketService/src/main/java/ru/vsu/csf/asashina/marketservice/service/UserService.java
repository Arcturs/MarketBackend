package ru.vsu.csf.asashina.marketservice.service;

import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.csf.asashina.marketservice.exception.ObjectAlreadyExistsException;
import ru.vsu.csf.asashina.marketservice.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketservice.exception.PasswordsDoNotMatchException;
import ru.vsu.csf.asashina.marketservice.mapper.UserMapper;
import ru.vsu.csf.asashina.marketservice.model.dto.RoleDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketservice.model.entity.User;
import ru.vsu.csf.asashina.marketservice.model.request.UserSignUpRequest;
import ru.vsu.csf.asashina.marketservice.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleService roleService;

    @Transactional
    public UserDTO signUpNewUser(UserSignUpRequest request) {
        if (isUserWithFollowingEmailExists(request.getEmail())) {
            throw new ObjectAlreadyExistsException("User with following email already exists");
        }

        if (!request.getPassword().equals(request.getRepeatPassword())) {
            throw new PasswordsDoNotMatchException("Passwords do not match");
        }

        User user = userMapper.toEntityFromSignUpRequest(request, hashPassword(request.getPassword()));
        UserDTO dto = userMapper.toDTOFromEntity(user);
        addUserRoleToUser(dto);
        User userWithRole = userMapper.toEntityFromDTO(dto);

        User savedUser = userRepository.save(userWithRole);
        return userMapper.toDTOFromEntity(savedUser);
    }

    private boolean isUserWithFollowingEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private void addUserRoleToUser(UserDTO user) {
        Set<RoleDTO> roles = user.getRoles();
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(roleService.getUserRole());
        user.setRoles(roles);
    }

    public void checkIfUserHasUserRole(UserDTO user) {
        if (!user.getRoles().contains(roleService.getUserRole())) {
            throw new AccessDeniedException("Access denied");
        }
    }

    public UserDTO getUserByEmail(String email) {
        User user = findUserByEmail(email);
        return userMapper.toDTOFromEntity(user);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ObjectNotExistException("User with following email does not exist")
        );
    }

    public boolean isUserAdmin(UserDTO user) {
        return user.getRoles().contains(roleService.getAdminRole());
    }
}
