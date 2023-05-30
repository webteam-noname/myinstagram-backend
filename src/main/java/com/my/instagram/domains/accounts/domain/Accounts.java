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
    private String username;
    private String password;
    private String name;

    private String profileName;
    private String profileIntro;
    private Long   profileImgFileId;

    // oauth 관련된 프로퍼티
    private String provider;
    private String providerId;

    @OneToMany(mappedBy = "accounts", cascade = CascadeType.PERSIST)
    List<AccountsRole> accountsRoles = new ArrayList<>();

    @Builder
    public Accounts(String username, String password, String name,String profileName, String profileIntro, Long profileImgFileId, String provider, String providerId) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.profileName = profileName;
        this.profileIntro = profileIntro;
        this.profileImgFileId = profileImgFileId;
        this.provider = provider;
        this.providerId = providerId;
    }

    // 프로파일을 수정합니다.
    public void updateProfile(ProfileUpdateRequest profileUpdateRequest){
        this.profileName      = profileUpdateRequest.getProfileName();
        this.profileIntro     = profileUpdateRequest.getProfileIntro();
        this.profileImgFileId = profileUpdateRequest.getProfileImgFileId();
    }

    // 비밀번호를 변경합니다.
    public void updatePassword(AccountsUpdateRequest accountsUpdateRequest) {
        this.password = accountsUpdateRequest.getPassword();
    }
}
