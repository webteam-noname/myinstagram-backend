package com.my.instagram.accounts;

import com.my.instagram.domains.accounts.dto.request.AccountsSaveRequest;
import com.my.instagram.domains.accounts.dto.request.ProfileUpdateRequest;
import com.my.instagram.domains.accounts.dto.response.AccountsResponse;
import com.my.instagram.domains.accounts.dto.response.ProfileSearchResponse;
import com.my.instagram.domains.accounts.service.AccountsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
public class AccountsServiceTest {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private EntityManager em;

    @Test
    void 회원을조회(){
        List<AccountsResponse> list = accountsService.searchAccounts("test");
        int count = list.size();

        assertThat(count).isEqualTo(20);
    }

    @Test
    void 중복회원체크(){
        AccountsSaveRequest accountsSaveRequest = new AccountsSaveRequest();
        accountsSaveRequest.setUsername("test0"+"@gmail.com");
        accountsSaveRequest.setPassword("1234");
        accountsSaveRequest.setProfileName("test0");
        accountsSaveRequest.setName("kim");

        accountsService.join(accountsSaveRequest);
    }

    @Test
    void 회원추천조회(){
        int currentPage = 0;
        Slice<AccountsResponse> accountsResponses = accountsService.searchSliceRecommendAccounts(currentPage);
        int count = accountsResponses.getContent().size();

        assertThat(count).isEqualTo(10);
    }

    @Test
    void 프로필수정_이미지등록없음(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setProfileName("test0");
        profileUpdateRequest.setChangeProfileName("수정_kimgun");
        profileUpdateRequest.setProfileIntro("수정_프로필 소개글입니다.");

        accountsService.updateProfile(profileUpdateRequest,null);

        em.flush();
        em.clear();

        ProfileSearchResponse searchProfile = accountsService.searchProfile("수정_kimgun");
        String profileName = searchProfile.getProfileName();

        assertThat(profileName).isEqualTo("수정_kimgun");
    }

    @Test
    void 프로필수정_이미지등록(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setProfileName("test0");
        profileUpdateRequest.setChangeProfileName("수정_kimgun");
        profileUpdateRequest.setProfileIntro("수정_프로필 소개글입니다.");

        byte[] fileContent = "Test file content".getBytes();
        MultipartFile file = new MockMultipartFile("testFile.txt", "testFile.txt", "text/plain", fileContent);

        accountsService.updateProfile(profileUpdateRequest, file);

        em.flush();
        em.clear();

        ProfileSearchResponse searchProfile = accountsService.searchProfile("수정_kimgun");
        String profileName = searchProfile.getProfileName();
        Long profileImgId  = searchProfile.getProfileImgFileId();

        assertThat(profileName).isEqualTo("수정_kimgun");
        assertThat(profileImgId).isNotZero();

    }
}
