package com.my.instagram.domains.accounts.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class AccountsSaveRequest {

    @NotNull(message = "아이디 입력은 필수입니다.")
    private String username;

    @NotNull(message = "비밀번호 입력은 필수입니다.")
    private String password;

    @NotNull(message = "회원 이름은 필수로 입력하셔야 합니다.")
    private String name;

    @NotNull(message = "사용자 이름을 입력하셔야 합니다.")
    private String profileName;

}

