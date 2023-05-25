package com.my.instagram.domains.accounts.dto.response;

import com.my.instagram.config.security.jwt.dto.JwtDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountsLoginResponse {
    JwtDto jwt;
    AccountsResponse accountsResponse;
}
