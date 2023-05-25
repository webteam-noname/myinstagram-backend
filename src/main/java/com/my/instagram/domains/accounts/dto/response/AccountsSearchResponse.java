package com.my.instagram.domains.accounts.dto.response;

import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.accounts.domain.AccountsRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString(of = {"username","password","name","accountsRoles", "createDate", "createUser"})
public class AccountsSearchResponse {
    private String username;
    private String password;
    private String name;
    private List<AccountsRole> accountsRoles = new ArrayList<>();
    private LocalDateTime createDate;
    private String createUser;

    public AccountsSearchResponse(Accounts acconts) {
        this.username      = acconts.getUsername();
        this.password      = acconts.getPassword();
        this.name          = acconts.getName();
        this.accountsRoles = acconts.getAccountsRoles();
        this.createDate    = acconts.getCreatedDate();
        this.createUser    = acconts.getUsername();
    }
}
