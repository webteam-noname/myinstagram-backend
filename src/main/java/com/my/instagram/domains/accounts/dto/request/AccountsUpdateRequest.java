package com.my.instagram.domains.accounts.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class AccountsUpdateRequest {
    @NotBlank(message = "프로필 명은 필수입니다.")
    private String profileName;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String password;

    private String authCode;
}
