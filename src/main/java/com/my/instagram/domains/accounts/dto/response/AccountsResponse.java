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
    private String profileName;
    private String name;

    public AccountsResponse(Accounts accounts) {
        this.username = accounts.getUsername();
        this.profileName = accounts.getProfileName();
        this.name = accounts.getName();
    }
}
