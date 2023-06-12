package com.my.instagram.domains.follow.dto.response;

import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.follow.domain.Follow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowSearchResponse {
    private Long accountsId;
    private String username;
    private String profileName;
    private String followName;
    private Character blockYn;

    public FollowSearchResponse(Follow follow) {
        this.accountsId = follow.getAccounts().getId();
        this.username = follow.getAccounts().getUsername();
        this.profileName = follow.getAccounts().getProfileName();
        this.followName = follow.getFollowName();
        this.blockYn = follow.getBlockYn();
    }
}
