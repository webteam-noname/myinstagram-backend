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
    @Column
    private Long id;

    private String username;
    private String follow;
    private Character blockYn;

    @Builder
    public Follow(String username, String follow, Character blockYn ) {
        this.username = username;
        this.follow = follow;
        this.blockYn = blockYn;
    }
}
