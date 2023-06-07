package com.my.instagram.domains.follow.dto.response;

import com.my.instagram.domains.follow.domain.Follow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowSaveResponse {
    private Long id;

    public FollowSaveResponse(Follow follow) {
        this.id = id;
    }
}
