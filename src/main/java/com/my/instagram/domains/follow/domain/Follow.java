package com.my.instagram.domains.follow.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Character followAccept;
    private Character blockYn;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounts_id")
    private Accounts accounts;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_accounts_id")
    @org.hibernate.annotations.Index(name = "follow_accounts_id_index")
    private Accounts followAccounts;

    @Builder
    public Follow(Accounts accounts, Accounts followAccounts, Character blockYn,Character followAccept) {
        this.accounts       = accounts;
        this.followAccounts = followAccounts;
        this.blockYn        = blockYn;
        this.followAccept  = followAccept;
    }

    public void setBlockYn(Character blockYn) {
        this.blockYn = blockYn;
    }

    public void setFollowAccept(Character followAccept) {
        this.followAccept = followAccept;
    }
}
