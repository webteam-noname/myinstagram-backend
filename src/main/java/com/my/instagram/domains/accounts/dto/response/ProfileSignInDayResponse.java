package com.my.instagram.domains.accounts.dto.response;

import com.my.instagram.common.file.domain.ImageFile;
import com.my.instagram.domains.accounts.domain.Accounts;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProfileSignInDayResponse {
    private LocalDateTime createdDate;
    private String profileImg;

    public ProfileSignInDayResponse(Accounts accounts){
        this.createdDate = accounts.getCreatedDate();
        this.profileImg = new ImageFile(accounts.getFiles()).get();
    }
}
