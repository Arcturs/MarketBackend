package ru.vsu.csf.asashina.marketservice.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserSignUpRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String surname;

    @NotBlank
    @Size(max = 255)
    @Email
    private String email;

    @Size(min = 8, max = 16)
    private String password;

    @Size(min = 8, max = 16)
    private String repeatPassword;
}
