package com.my.instagram.domains.follow.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class FollowApproveRequest {
    @NotEmpty(message = "프로필명은 필수입니다.")
    private String profileName;

    @NotEmpty(message = "팔로워는 필수입니다.")
    private String followName;

    private Character followAccept;
}
