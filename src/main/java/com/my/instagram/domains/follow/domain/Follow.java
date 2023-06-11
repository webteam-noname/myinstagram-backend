package com.my.instagram.domains.follow.domain;

import com.my.instagram.domains.accounts.domain.Accounts;
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
    private String followName;
    private Character blockYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounts_id")
    private Accounts accounts;

    @Builder
    public Follow(Accounts accounts, String followName, Character blockYn ) {
        this.accounts    = accounts;
        this.followName  = followName;
        this.blockYn     = blockYn;
    }

}
