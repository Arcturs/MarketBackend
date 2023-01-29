package ru.vsu.csf.asashina.marketserver.service;

import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.csf.asashina.marketserver.exception.ObjectAlreadyExistsException;
import ru.vsu.csf.asashina.marketserver.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketserver.exception.PasswordsDoNotMatchException;
import ru.vsu.csf.asashina.marketserver.exception.WrongCredentialsException;
import ru.vsu.csf.asashina.marketserver.mapper.UserMapper;
import ru.vsu.csf.asashina.marketserver.model.dto.RoleDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.User;
import ru.vsu.csf.asashina.marketserver.model.request.LoginRequest;
import ru.vsu.csf.asashina.marketserver.model.request.UserSignUpRequest;
import ru.vsu.csf.asashina.marketserver.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleService roleService;

    @Transactional
    public void signUpNewUser(UserSignUpRequest request) {
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

        userRepository.save(userWithRole);
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

    public void loginUser(LoginRequest request) {
        if (!isUserWithFollowingEmailExists(request.getEmail())) {
            throw new WrongCredentialsException("Wrong email or password");
        }

        User user = findUserByEmail(request.getEmail());
        checkIFPasswordsMatchWhenLogin(request.getPassword(), user.getPasswordHash());

        UserDTO dto = userMapper.toDTOFromEntity(user);
        checkIfUserHasUserRole(dto);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ObjectNotExistException("User with following email does not exist")
        );
    }

    private void checkIFPasswordsMatchWhenLogin(String requestPassword, String hashPassword) {
        if(!BCrypt.checkpw(requestPassword, hashPassword)) {
            throw new WrongCredentialsException("Wrong email or password");
        }
    }

    private void checkIfUserHasUserRole(UserDTO user) {
        if (!user.getRoles().contains(roleService.getUserRole())) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
