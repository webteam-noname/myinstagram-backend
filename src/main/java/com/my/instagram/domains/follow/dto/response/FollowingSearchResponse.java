package com.my.instagram.domains.follow.dto.response;

import com.my.instagram.common.file.domain.ImageFile;
import com.my.instagram.domains.follow.domain.Follow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowingSearchResponse {
    private Long accountId;
    private String username;
    private String profileName;
    private Long followAccountId;
    private String followUsername;
    private String followName;
    private Character blockYn;
    private String profileImg;

    public FollowingSearchResponse(Follow follow) {
        this.accountId = follow.getAccounts().getId();
        this.username = follow.getAccounts().getUsername();
        this.profileName = follow.getAccounts().getProfileName();
        this.followAccountId =follow.getFollowAccounts().getId();
        this.followUsername = follow.getFollowAccounts().getUsername();
        this.followName = follow.getFollowAccounts().getProfileName();
        this.blockYn = follow.getBlockYn();
        this.profileImg = new ImageFile(follow.getAccounts().getFiles()).get();
    }

    @Override
    public String toString() {
        return "FollowingSearchResponse{" +
                "accountId=" + accountId +
                ", username='" + username + '\'' +
                ", profileName='" + profileName + '\'' +
                ", followAccountId=" + followAccountId +
                ", followUsername='" + followUsername + '\'' +
                ", followName='" + followName + '\'' +
                ", blockYn=" + blockYn +
                '}';
    }
}
