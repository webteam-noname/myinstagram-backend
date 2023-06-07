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
    private String follow;
    private Character blockYn;

    public FollowSearchResponse(Follow follow) {
        this.username = follow.getUsername();
        this.follow = follow.getFollow();
        this.blockYn = follow.getBlockYn();
    }
}
