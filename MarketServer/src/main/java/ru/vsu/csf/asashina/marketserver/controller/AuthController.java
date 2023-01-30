package ru.vsu.csf.asashina.marketserver.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.csf.asashina.marketserver.model.ResponseBuilder;
import ru.vsu.csf.asashina.marketserver.model.request.LoginRequest;
import ru.vsu.csf.asashina.marketserver.model.request.RefreshTokenRequest;
import ru.vsu.csf.asashina.marketserver.model.request.UserSignUpRequest;
import ru.vsu.csf.asashina.marketserver.service.AuthService;
import ru.vsu.csf.asashina.marketserver.service.TokenService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUpNewUserUsingForm(@RequestBody @Valid UserSignUpRequest request) {
        return ResponseBuilder.build(OK, authService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest request) {
        return ResponseBuilder.build(OK, authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseBuilder.build(OK, tokenService.refreshAccessToken(request));
    }
}
