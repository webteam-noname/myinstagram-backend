package com.my.instagram.domains.accounts.dto.response;

import com.my.instagram.domains.accounts.domain.Accounts;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateResponse {
    private String profileName;
    private String profileIntro;

    public ProfileUpdateResponse(Accounts accounts){
        this.profileName = accounts.getProfileName();
        this.profileIntro = accounts.getProfileIntro();
    }
}
