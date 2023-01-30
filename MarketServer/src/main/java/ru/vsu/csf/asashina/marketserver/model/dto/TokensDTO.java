package ru.vsu.csf.asashina.marketserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TokensDTO {

    private String accessToken;
    private String refreshToken;
}
