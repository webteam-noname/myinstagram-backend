package com.my.instagram.domains.accounts.dto.response;

import com.my.instagram.domains.accounts.domain.Accounts;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountsResponse {
    private String username;
    private String name;

    public AccountsResponse(Accounts accounts) {
        this.username = accounts.getUsername();
        this.name = accounts.getName();
    }
}
