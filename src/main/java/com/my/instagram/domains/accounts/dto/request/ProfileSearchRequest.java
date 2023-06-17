package com.my.instagram.domains.accounts.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class ProfileSearchRequest {

    @NotEmpty(message = "아이디 입력은 필수입니다.")
    private String username;

}
