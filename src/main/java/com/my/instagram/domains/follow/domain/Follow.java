package com.my.instagram.domains.follow.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;
    private String username;
    private String followName;
    private Character blockYn;

    @Builder
    public Follow(String username, String followName, Character blockYn ) {
        this.username   = username;
        this.followName = followName;
        this.blockYn    = blockYn;
    }

}
