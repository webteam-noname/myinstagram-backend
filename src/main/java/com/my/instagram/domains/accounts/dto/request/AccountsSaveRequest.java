package com.my.instagram.domains.accounts.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class AccountsSaveRequest {

    @NotEmpty(message = "아이디 입력은 필수입니다.")
    private String username;

    @NotEmpty(message = "비밀번호 입력은 필수입니다.")
    private String password;

    @NotEmpty(message = "회원 이름은 필수로 입력하셔야 합니다.")
    private String name;

    @NotEmpty(message = "프로필 명을 입력하셔야 합니다.")
    private String profileName;

    @NotEmpty(message = "인증코드 입력은 필수입니다.")
    private String authCode;

}

