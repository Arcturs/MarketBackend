package ru.vsu.csf.asashina.marketservice.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.csf.asashina.marketservice.exception.WrongCredentialsException;
import ru.vsu.csf.asashina.marketservice.model.dto.TokensDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketservice.model.request.LoginRequest;
import ru.vsu.csf.asashina.marketservice.model.request.UserSignUpRequest;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserService userService;
    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;

    @Transactional
    public TokensDTO signUp(UserSignUpRequest request) {
        UserDTO user = userService.signUpNewUser(request);
        return tokenService.createTokens(user);
    }

    @Transactional
    public TokensDTO login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new WrongCredentialsException("Wrong email or password");
        }
        UserDTO user = userService.getUserByEmail(request.getEmail());
        userService.checkIfUserHasUserRole(user);
        return tokenService.createTokens(user);
    }
}
