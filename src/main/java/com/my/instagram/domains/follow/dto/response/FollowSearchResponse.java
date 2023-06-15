package com.my.instagram.domains.follow.dto.response;

import com.my.instagram.common.file.domain.Files;
import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.follow.domain.Follow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowSearchResponse {
    private Long accountId;
    private String username;
    private String profileName;
    private String followUsername;
    private String followName;
    private Character blockYn;
    private String profileImg;

    public FollowSearchResponse(Follow follow) {
        this.accountId = follow.getAccounts().getId();
        this.username = follow.getAccounts().getUsername();
        this.profileName = follow.getAccounts().getProfileName();
        this.followUsername = follow.getFollowUsername();
        this.followName = follow.getFollowName();
        this.blockYn = follow.getBlockYn();
    }

    @Override
    public String toString() {
        return "FollowSearchResponse{" +
                "accountId=" + accountId +
                ", username='" + username + '\'' +
                ", profileName='" + profileName + '\'' +
                ", followUsername='" + followUsername + '\'' +
                ", followName='" + followName + '\'' +
                ", blockYn=" + blockYn +
                ", profileImg='" + profileImg + '\'' +
                '}';
    }
}
