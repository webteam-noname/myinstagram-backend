package com.my.instagram.domains.accounts.dto.request;

import com.my.instagram.domains.accounts.domain.Accounts;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateRequest {
    @NotEmpty(message = "프로필 명을 입력하셔야 합니다.")
    private String profileName;

    private String changeProfileName;

    private String profileIntro;

    private Long profileImgFileId;

}
