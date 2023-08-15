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
    private String profileImg;

    // 삭제 예정
    private Long profileImgFileId;

    // 2023-08-14 변경사항
    // 여전히 Files에대한 의존성이 있고, profileImg를 봐도 여전히 결합도가 높다.
    // 하지만 이전에 비해선 Files에 대한 캡슐화가 일부 진행되었다 생각한다.
    public ProfileSearchResponse(Accounts accounts) {
        this.profileName  = accounts.getProfileName();
        this.profileIntro = accounts.getProfileIntro();
        this.profileImg   = accounts.getFiles().getImageFile(); // file 프로퍼티가 없으면 null로 나옴.. 수정 필요
    }

    public ProfileSearchResponse(Accounts accounts, Files file) {
        this.profileName      = accounts.getProfileName();
        this.profileIntro     = accounts.getProfileIntro();
        this.profileImgFileId = accounts.getProfileImgFileId();

        if(file != null){
            this.profileImg = file.getFileName()+"."+file.getFileExt();
        }else{
            this.profileImg = "no-image.jpg";
        }
    }
}
