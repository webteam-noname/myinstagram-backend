package com.my.instagram.domains.accounts.domain;

import com.my.instagram.common.file.domain.Files;
import com.my.instagram.domains.accounts.dto.request.AccountsUpdateRequest;
import com.my.instagram.domains.accounts.dto.request.ProfileUpdateRequest;
import com.my.instagram.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Accounts extends BaseEntity {
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
    private Long profileImgFileId;
    private Character checkAuthYn;

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
                    Long profileImgFileId,
                    String provider,
                    String providerId,
                    Character checkAuthYn) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.profileName = profileName;
        this.profileIntro = profileIntro;
        this.provider = provider;
        this.providerId = providerId;
        this.checkAuthYn = checkAuthYn;
    }

    // 프로파일을 수정합니다.
    public void updateProfile(ProfileUpdateRequest profileUpdateRequest){
        if(profileUpdateRequest.getChangeProfileName() != null && profileUpdateRequest.getChangeProfileName() != ""){
            this.profileName      = profileUpdateRequest.getChangeProfileName();
        }

        if(profileUpdateRequest.getProfileIntro() != null){
            this.profileIntro     = profileUpdateRequest.getProfileIntro();
        }

        if(profileUpdateRequest.getProfileImgFileId() != null){
            this.profileImgFileId = profileUpdateRequest.getProfileImgFileId();
        }
    }

    // 비밀번호를 변경합니다.
    public void updatePassword(String changePassword) {
        this.password = changePassword;
    }

    public void updateCheckAuthY() {
        this.checkAuthYn = 'Y';
    }
}
