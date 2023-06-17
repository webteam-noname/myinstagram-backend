package com.my.instagram.domains.accounts.dto.request;

import com.my.instagram.domains.accounts.domain.Accounts;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateRequest {
    @NotBlank(message = "프로필명은 필수입니다.")
    private String profileName;

    private String changeProfileName;

    private String profileIntro;

    private Long profileImgFileId;

}
