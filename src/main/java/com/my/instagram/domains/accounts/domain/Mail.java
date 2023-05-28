package com.my.instagram.domains.accounts.domain;

import com.my.instagram.common.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_id")
    private Long id;
    private String username;
    private String authCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounts_id")
    private Accounts accounts;

    @Builder
    public Mail(String username, String authCode) {
        this.username = username;
        this.authCode = authCode;
    }


}
