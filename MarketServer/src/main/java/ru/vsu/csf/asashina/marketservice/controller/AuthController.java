package ru.vsu.csf.asashina.marketservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.csf.asashina.marketservice.model.ResponseBuilder;
import ru.vsu.csf.asashina.marketservice.model.dto.ExceptionDTO;
import ru.vsu.csf.asashina.marketservice.model.dto.TokensDTO;
import ru.vsu.csf.asashina.marketservice.model.request.LoginRequest;
import ru.vsu.csf.asashina.marketservice.model.request.RefreshTokenRequest;
import ru.vsu.csf.asashina.marketservice.model.request.UserSignUpRequest;
import ru.vsu.csf.asashina.marketservice.service.AuthService;
import ru.vsu.csf.asashina.marketservice.service.TokenService;

import static org.springframework.http.HttpStatus.OK;
import static ru.vsu.csf.asashina.marketservice.model.constant.Tag.AUTH;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/sign-up")
    @Operation(summary = "Signs up new user", tags = AUTH, responses = {
            @ApiResponse(responseCode = "200", description = "User was successfully registered and tokens are returned", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TokensDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "409", description = "User with following email already exists", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    @SecurityRequirements
    public ResponseEntity<?> signUpNewUserUsingForm(@RequestBody @Valid UserSignUpRequest request) {
        return ResponseBuilder.build(OK, authService.signUp(request));
    }

    @PostMapping("/login")
    @Operation(tags = AUTH, responses = {
            @ApiResponse(responseCode = "200", description = "User was successfully logged in and tokens are returned", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TokensDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Wrong credentials", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "401", description = "User does not have needed role", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    @SecurityRequirements
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest request) {
        return ResponseBuilder.build(OK, authService.login(request));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refreshes access token using refresh token", tags = AUTH, responses = {
            @ApiResponse(responseCode = "200", description = "Tokens are returned", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TokensDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Refresh token is empty", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "401", description = "Refresh token is expired", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Refresh token does not exist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    @SecurityRequirements
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseBuilder.build(OK, tokenService.refreshAccessToken(request));
    }
}
