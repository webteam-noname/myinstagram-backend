package com.my.instagram.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.instagram.config.security.jwt.JwtProvider;
import com.my.instagram.domains.accounts.dto.request.AccountsLoginReqeust;
import com.my.instagram.domains.accounts.dto.request.AccountsSaveRequest;
import com.my.instagram.domains.accounts.dto.request.ProfileUpdateRequest;
import com.my.instagram.domains.accounts.dto.response.AccountsLoginResponse;
import com.my.instagram.domains.accounts.dto.response.AccountsResponse;
import com.my.instagram.domains.accounts.dto.response.ProfileSearchResponse;
import com.my.instagram.domains.accounts.service.AccountsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(SpringExtension.class)
public class AccountsServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtProvider jwtProvider;

    @Test
    void 로그인(){
        AccountsLoginReqeust accountsLoginReqeust = new AccountsLoginReqeust();
        accountsLoginReqeust.setUsername("test0@gmail.com");
        accountsLoginReqeust.setPassword("1234");
        AccountsLoginResponse login = accountsService.login(accountsLoginReqeust);


        System.out.println(login.getJwt().getAccessToken());
    }

    @Test
    void 회원을조회(){
        List<AccountsResponse> list = accountsService.searchAccounts("test");
        int count = list.size();

        assertThat(count).isEqualTo(20);
    }

    @Test
    void 중복회원체크_유저명이같을경우(){
        AccountsSaveRequest accountsSaveRequest = new AccountsSaveRequest();
        accountsSaveRequest.setUsername("test0@gmail.com");
        accountsSaveRequest.setPassword("1234");
        accountsSaveRequest.setProfileName("test0");
        accountsSaveRequest.setName("kim");

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            accountsService.signUp(accountsSaveRequest);
        });

        assertThat(runtimeException.getMessage()).isEqualTo("사용자 ID는 중복될 수 없습니다.");
    }

    @Test
    void 중복회원체크_프로필명이같을경우(){
        AccountsSaveRequest accountsSaveRequest = new AccountsSaveRequest();
        accountsSaveRequest.setUsername("test1234@gmail.com");
        accountsSaveRequest.setPassword("1234");
        accountsSaveRequest.setProfileName("test0");
        accountsSaveRequest.setName("kim");

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            accountsService.signUp(accountsSaveRequest);
        });

        assertThat(runtimeException.getMessage()).isEqualTo("프로필 명은 중복될 수 없습니다.");
    }

    @Test
    void 회원가입_필수입력안함() throws Exception {
        // Given
        AccountsSaveRequest request = new AccountsSaveRequest();
        request.setUsername(null);
        request.setPassword(null);
        request.setProfileName(null);
        request.setName(null);

        String saveDtoJsonString = objectMapper.writeValueAsString(request);
        System.out.println(saveDtoJsonString);
        // Generate a valid JWT token
        String jwtToken = generateValidJwtToken(); // 유효한 JWT 토큰 생성하는 함수 (구현 필요)

        MvcResult result = mockMvc.perform(post("/api/auth/accounts/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken) // JWT 토큰을 Authorization 헤더에 포함
                        .content(saveDtoJsonString))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation Failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.password").value("비밀번호 입력은 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.username").value("아이디 입력은 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.name").value("회원 이름은 필수로 입력하셔야 합니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.profileName").value("프로필 명을 입력하셔야 합니다."))
                .andReturn();

    }

    private String generateValidJwtToken() {
        return jwtProvider.createAccessToken("test1@gmail.com", "kim","ROLE_USER");
    }

    @Test
    void 회원추천조회(){
        int currentPage = 0;
        Slice<AccountsResponse> accountsResponses = accountsService.searchSliceRecommendAccounts(currentPage);
        int count = accountsResponses.getContent().size();

        assertThat(count).isEqualTo(10);
    }

    @Test
    void 프로필수정_프로필명중복(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setProfileName("test0");
        profileUpdateRequest.setChangeProfileName("test0");

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            accountsService.updateProfile(profileUpdateRequest,null);
        });

        assertThat(runtimeException.getMessage()).isEqualTo("프로필 명은 중복될 수 없습니다.");
    }

    @Test
    void 프로필_필수입력안함() throws Exception {
        // Given
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setProfileName("");

        String saveDtoJsonString = objectMapper.writeValueAsString(profileUpdateRequest);
        System.out.println(saveDtoJsonString);
        // Generate a valid JWT token
        String jwtToken = generateValidJwtToken(); // 유효한 JWT 토큰 생성하는 함수 (구현 필요)

        MvcResult result = mockMvc.perform(put("/api/accounts/test0/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken) // JWT 토큰을 Authorization 헤더에 포함
                        .content(saveDtoJsonString))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation Failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.profileName").value("프로필 명을 입력하셔야 합니다."))
                .andReturn();
    }

    @Test
    void 프로필수정_이미지등록없음(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setProfileName("test0");
        profileUpdateRequest.setChangeProfileName("수정_kimgun");
        profileUpdateRequest.setProfileIntro("수정_프로필 소개글입니다.");

        accountsService.updateProfile(profileUpdateRequest,null);

        ProfileSearchResponse searchProfile = accountsService.searchProfile("수정_kimgun");
        String profileName = searchProfile.getProfileName();
        String profileIntro = searchProfile.getProfileIntro();

        assertThat(profileName).isEqualTo("수정_kimgun");
        assertThat(profileIntro).isEqualTo("수정_프로필 소개글입니다.");
    }

    @Test
    void 프로필수정_데이터빈값일경우_체크(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setProfileName("test0");
        profileUpdateRequest.setChangeProfileName("");
        profileUpdateRequest.setProfileIntro("");
        profileUpdateRequest.setProfileImgFileId(229L);

        accountsService.updateProfile(profileUpdateRequest,null);

        ProfileSearchResponse searchProfile = accountsService.searchProfile("test0");
        String profileName = searchProfile.getProfileName();
        String profileIntro = searchProfile.getProfileIntro();
        String profileImg = searchProfile.getProfileImg();

        assertThat(profileName).isNotEqualTo("");
        assertThat(profileIntro).isEqualTo("");
        assertThat(profileImg).isEqualTo("c:/files/no-image.jpg");
    }

    @Test
    void 프로필수정_이미지ID있음_파일no(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setProfileName("test0");
        profileUpdateRequest.setChangeProfileName("수정_kimgun");
        profileUpdateRequest.setProfileIntro("수정_프로필 소개글입니다.");

        byte[] fileContent = "Test file content".getBytes();
        MultipartFile file = new MockMultipartFile("profileImg",
                "profile.jpg",
                "image/jpeg",
                fileContent);

        accountsService.updateProfile(profileUpdateRequest, file);

        ProfileSearchResponse searchProfile = accountsService.searchProfile("수정_kimgun");
        String profileName = searchProfile.getProfileName();
        String profileIntro = searchProfile.getProfileIntro();
        String profileImg = searchProfile.getProfileImg();

        assertThat(profileName).isEqualTo("수정_kimgun");
        assertThat(profileIntro).isEqualTo("수정_프로필 소개글입니다.");
        assertThat(profileImg).isNotNull();
        assertThat(profileImg).isNotEmpty();
        assertThat(profileImg).isNotEqualTo("c:/files/no-image.jpg");
    }

    @Test
    void 프로필수정_이미지삭제후_재등록(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setProfileName("test0");
        profileUpdateRequest.setChangeProfileName("");
        profileUpdateRequest.setProfileIntro("");
        profileUpdateRequest.setProfileImgFileId(229L);

        accountsService.updateProfile(profileUpdateRequest,null);

        profileUpdateRequest.setProfileName("test0");
        profileUpdateRequest.setChangeProfileName("수정_kimgun");
        profileUpdateRequest.setProfileIntro("수정_프로필 소개글입니다.");

        byte[] fileContent = "Test file content".getBytes();
        MultipartFile file = new MockMultipartFile("profileImg",
                "profile.jpg",
                "image/jpeg",
                fileContent);

        accountsService.updateProfile(profileUpdateRequest, file);

        ProfileSearchResponse searchProfile = accountsService.searchProfile("수정_kimgun");
        String profileName = searchProfile.getProfileName();
        String profileIntro = searchProfile.getProfileIntro();
        String profileImg = searchProfile.getProfileImg();

        assertThat(profileName).isEqualTo("수정_kimgun");
        assertThat(profileIntro).isEqualTo("수정_프로필 소개글입니다.");
        assertThat(profileImg).isNotNull();
        assertThat(profileImg).isNotEmpty();
        assertThat(profileImg).isNotEqualTo("c:/files/no-image.jpg");
    }

    @Test
    void 프로필수정_존재하지않는회원(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setProfileName("test1234");
        profileUpdateRequest.setChangeProfileName("");
        profileUpdateRequest.setProfileIntro("");

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            accountsService.updateProfile(profileUpdateRequest, null);
        });

        assertThat("유저를 조회할 수 없습니다.").isEqualTo(runtimeException.getMessage());
    }

    @Test
    void 프로필수정_이미지등록(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setProfileName("test0");
        profileUpdateRequest.setChangeProfileName("수정_kimgun");
        profileUpdateRequest.setProfileIntro("수정_프로필 소개글입니다.");

        byte[] fileContent = "Test file content".getBytes();
        MultipartFile file = new MockMultipartFile("profileImg",
                "profile.jpg",
                "image/jpeg",
                fileContent);

        accountsService.updateProfile(profileUpdateRequest, file);

        em.flush();
        em.clear();

        ProfileSearchResponse searchProfile = accountsService.searchProfile("수정_kimgun");
        String profileName = searchProfile.getProfileName();
        Long profileImgId  = searchProfile.getProfileImgFileId();

        assertThat(profileName).isEqualTo("수정_kimgun");
        assertThat(profileImgId).isNotZero();
    }

    @Test
    void 프로필수정_이미등록된이미지_null입력(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setProfileName("test0");
        profileUpdateRequest.setChangeProfileName("");
        profileUpdateRequest.setProfileIntro("");
        profileUpdateRequest.setProfileImgFileId(1L);

        accountsService.updateProfile(profileUpdateRequest, null);

        ProfileSearchResponse searchProfile = accountsService.searchProfile("test0");
        System.out.println(searchProfile.getProfileImg());
    }
}