package com.my.instagram.config.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtDto {
    @NotNull(message = "타입값은 필수입니다.")
    private String type;

    @NotNull(message = "토큰값은 필수입니다.")
    private String accessToken;

    private String refreshToken;
}
