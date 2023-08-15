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

    private String changeProfileName;

    private String profileIntro;

    // 2023-08-14 추후 삭제 예정
    private Long profileImgFileId;

}
