package ru.vsu.csf.asashina.marketserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.csf.asashina.marketserver.model.ResponseBuilder;
import ru.vsu.csf.asashina.marketserver.model.dto.ExceptionDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.TokensDTO;
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

    private final static String AUTH_TAG = "Auth";

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/sign-up")
    @Operation(summary = "Signs up new user", tags = AUTH_TAG, responses = {
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
    public ResponseEntity<?> signUpNewUserUsingForm(@RequestBody @Valid UserSignUpRequest request) {
        return ResponseBuilder.build(OK, authService.signUp(request));
    }

    @PostMapping("/login")
    @Operation(tags = AUTH_TAG, responses = {
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
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest request) {
        return ResponseBuilder.build(OK, authService.login(request));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refreshes access token using refresh token", tags = AUTH_TAG, responses = {
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
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseBuilder.build(OK, tokenService.refreshAccessToken(request));
    }
}
