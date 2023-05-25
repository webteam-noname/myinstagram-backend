package com.my.instagram.domains.accounts.dto.response;

import com.my.instagram.domains.accounts.domain.Accounts;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileSearchResponse {
    private String profileName;
    private String profileImg;
    private String profileIntro;

    public ProfileSearchResponse(Accounts accounts) {
        this.profileName  = accounts.getProfileName();
        this.profileImg   = accounts.getProfileImg();
        this.profileIntro = accounts.getProfileIntro();
    }
}
