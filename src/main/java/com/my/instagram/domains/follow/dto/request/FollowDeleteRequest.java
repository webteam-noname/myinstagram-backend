package com.my.instagram.domains.follow.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class FollowDeleteRequest {
    @NotBlank(message = "아이디 입력은 필수입니다.")
    private String username;

    @NotBlank(message = "팔로워는 필수입니다.")
    private String follow;
}
