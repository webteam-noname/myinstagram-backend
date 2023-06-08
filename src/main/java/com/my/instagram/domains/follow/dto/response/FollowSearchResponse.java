package com.my.instagram.domains.follow.dto.response;

import com.my.instagram.domains.follow.domain.Follow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowSearchResponse {
    private String username;
    private String followName;
    private Character blockYn;

    public FollowSearchResponse(Follow follow) {
        this.username = follow.getUsername();
        this.followName = follow.getFollowName();
        this.blockYn = follow.getBlockYn();
    }
}
