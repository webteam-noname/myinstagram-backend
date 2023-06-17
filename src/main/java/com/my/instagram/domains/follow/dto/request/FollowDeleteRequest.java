package com.my.instagram.domains.follow.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class FollowDeleteRequest {
    @NotEmpty(message = "아이디 입력은 필수입니다.")
    private String ProfileName;

    @NotEmpty(message = "팔로워는 필수입니다.")
    private String followName;
}
