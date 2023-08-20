package com.my.instagram.domains.accounts.dto.response;

import com.my.instagram.common.file.domain.ImageFile;
import com.my.instagram.domains.accounts.domain.Accounts;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileSearchResponse {
    private String profileName;
    private String profileIntro;
    private String profileImg;

    public ProfileSearchResponse(Accounts accounts) {
        this.profileName  = accounts.getProfileName();
        this.profileIntro = accounts.getProfileIntro();
        this.profileImg   = new ImageFile(accounts.getFiles()).get();
    }

    @Override
    public String toString() {
        return "ProfileSearchResponse{" +
                "profileName='" + profileName + '\'' +
                ", profileIntro='" + profileIntro + '\'' +
                ", profileImg='" + profileImg + '\'' +
                '}';
    }
}
