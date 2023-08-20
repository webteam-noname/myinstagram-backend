package com.my.instagram.domains.accounts.domain;

import com.my.instagram.common.domain.BaseEntity;
import com.my.instagram.common.file.domain.Files;
import com.my.instagram.common.file.domain.FileSaveType;
import com.my.instagram.domains.accounts.dto.request.ProfileUpdateRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Accounts extends BaseEntity implements FileSaveType {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accounts_id")
    private Long id;

    @Column(name = "follow_accounts_id")
    private Long followAccountsId;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String password;

    private String name;

    @Column(unique = true, nullable = false)
    private String profileName;
    private String profileIntro;

    // 2023-08-12 파일 적용방식 변경
    // 첨부파일을 사용하는 객체에서 파일을 관리하는 것으로 제작하려고 함
    // 지금 당장은 이 방식이 N+1을 해결하는 방법이라 생각이 든다.
    @OneToOne(fetch= FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="file_id")
    private Files files;

    // oauth 관련된 프로퍼티
    private String provider;
    private String providerId;

    @OneToMany(mappedBy = "accounts", cascade = CascadeType.PERSIST)
    List<AccountsRole> accountsRoles = new ArrayList<>();

    @PostPersist
    public void setFollowAccountsId() {
        this.followAccountsId = this.id;
    }

    @Builder
    public Accounts(String username,
                    String password,
                    String name,
                    String profileName,
                    String profileIntro,
                    String provider,
                    String providerId) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.profileName = profileName;
        this.profileIntro = profileIntro;
        this.provider = provider;
        this.providerId = providerId;
    }

    // 2023-08-15 변경 내용
    // 파일에 대한 저장및 수정은 file 엔티티에서 진행될 것이다.
    // 그래서 다음의 소스를 주석 처리하였다.
    // 프로파일을 수정합니다.
    public void updateProfile(ProfileUpdateRequest profileUpdateRequest){
        if(profileUpdateRequest.getChangeProfileName() != null && profileUpdateRequest.getChangeProfileName() != ""){
            this.profileName      = profileUpdateRequest.getChangeProfileName();
        }

        if(profileUpdateRequest.getProfileIntro() != null){
            this.profileIntro     = profileUpdateRequest.getProfileIntro();
        }
    }

    @Override
    public void saveFiles(Files files) {
        this.files = files;
    }

    // 비밀번호를 변경합니다.
    public void updatePassword(String changePassword) {
        this.password = changePassword;
    }

}
