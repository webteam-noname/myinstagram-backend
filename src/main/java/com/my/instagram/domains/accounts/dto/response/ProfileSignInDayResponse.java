package com.my.instagram.domains.accounts.dto.response;

import com.my.instagram.common.file.domain.Files;
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

    public ProfileSignInDayResponse(Accounts accounts, Files file){
        this.createdDate = accounts.getCreatedDate();
        if(file.getFileName() != null){
            this.profileImg = file.getFileName()+"."+file.getFileExt();
        }else{
            this.profileImg = "no-image.jpg";
        }
    }
}
