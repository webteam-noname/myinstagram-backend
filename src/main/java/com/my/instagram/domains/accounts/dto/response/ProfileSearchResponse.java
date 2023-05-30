package com.my.instagram.domains.accounts.dto.response;

import com.my.instagram.common.file.domain.Files;
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
    private Long profileImgFileId;
    private String profileImg;

    public ProfileSearchResponse(Accounts accounts, Files file) {
        this.profileName      = accounts.getProfileName();
        this.profileIntro     = accounts.getProfileIntro();
        this.profileImgFileId = accounts.getProfileImgFileId();
        this.profileImg       = file.getFilePath()+file.getFileName()+"."+file.getFileExt();
    }
}
