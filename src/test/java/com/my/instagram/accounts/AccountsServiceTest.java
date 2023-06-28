package com.my.instagram.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.instagram.config.security.jwt.JwtProvider;
import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.accounts.dto.request.AccountsLoginReqeust;
import com.my.instagram.domains.accounts.dto.request.AccountsSaveRequest;
import com.my.instagram.domains.accounts.dto.request.AccountsUpdatePasswordRequest;
import com.my.instagram.domains.accounts.dto.request.ProfileUpdateRequest;
import com.my.instagram.domains.accounts.dto.response.AccountsResponse;
import com.my.instagram.domains.accounts.dto.response.ProfileSearchResponse;
import com.my.instagram.domains.accounts.dto.response.ProfileSignInDayResponse;
import com.my.instagram.domains.accounts.repository.AccountsRepository;
import com.my.instagram.domains.accounts.service.AccountsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private AccountsService accountsService;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtProvider jwtProvider;

    @Test
    void 로그인_필수입력안함() throws Exception {
        // Given
        AccountsLoginReqeust accountsLoginReqeust = new AccountsLoginReqeust();
        accountsLoginReqeust.setUsername(null);
        accountsLoginReqeust.setPassword(null);

        String saveDtoJsonString = objectMapper.writeValueAsString(accountsLoginReqeust);

        String jwtToken = testGenerateValidJwtToken();

        MvcResult result = mockMvc.perform(post("/api/auth/accounts/sign-ins")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveDtoJsonString))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation Failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.username").value("아이디 입력은 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.password").value("비밀번호 입력은 필수입니다."))
                .andReturn();

    }

    @Test
    void 로그인_비밀번호틀림(){
        AccountsLoginReqeust accountsLoginReqeust = new AccountsLoginReqeust();
        accountsLoginReqeust.setUsername("test4@gmail.com");
        accountsLoginReqeust.setPassword("123");

        RuntimeException runtimeException = assertThrows(BadCredentialsException.class, () -> {
            accountsService.login(accountsLoginReqeust);
        });

        assertThat(runtimeException.getMessage()).isEqualTo("자격 증명에 실패하였습니다.");

    }

    @Test
    @WithMockUser
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
        String jwtToken = testGenerateValidJwtToken(); // 유효한 JWT 토큰 생성하는 함수 (구현 필요)

        MvcResult result = mockMvc.perform(post("/api/auth/accounts/sign-ups")
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

    @Test
    void 회원을조회(){
        List<AccountsResponse> list = accountsService.searchAccounts("test");
        int count = list.size();

        assertThat(count).isEqualTo(20);
    }

    @Test
    void 회원가입_유저명중복(){
        AccountsSaveRequest accountsSaveRequest = new AccountsSaveRequest();
        accountsSaveRequest.setUsername("etkim02@naver.com");
        accountsSaveRequest.setPassword("1234");
        accountsSaveRequest.setProfileName("test0");
        accountsSaveRequest.setName("kim");

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            accountsService.signUp(accountsSaveRequest);
        });

        assertThat(runtimeException.getMessage()).isEqualTo("사용자 ID는 중복될 수 없습니다.");
    }

    @Test
    void 회원가입_프로필명중복(){
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


    private String testGenerateValidJwtToken() {
        return jwtProvider.createAccessToken("test4@gmail.com", "test4","ROLE_USER");
    }

    private String generateValidJwtToken(String username, String name) {
        return jwtProvider.createAccessToken(username, name,"ROLE_USER");
    }

    @Test
    void 회원추천조회(){
        int currentPage = 0;
        Slice<AccountsResponse> accountsResponses = accountsService.searchSliceRecommendAccounts(currentPage);
        int count = accountsResponses.getContent().size();

        assertThat(count).isEqualTo(10);
    }

    @Test
    void 비밀번호변경(){
        AccountsUpdatePasswordRequest accountsUpdatePasswordRequest = new AccountsUpdatePasswordRequest();
        accountsUpdatePasswordRequest.setUidb("5d63");
        accountsUpdatePasswordRequest.setPassword("123456");
        accountsUpdatePasswordRequest.setCheckPassword("123456");
        String s = accountsService.updatePassword(accountsUpdatePasswordRequest);

        Accounts accounts = accountsRepository.findByProfileName("test0").get();

        assertThat(accounts.getPassword()).isEqualTo("123456");
    }

    @Test
    void 프로필수정_프로필명중복(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setChangeProfileName("test0");

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            accountsService.updateProfile("test0", profileUpdateRequest,null);
        });

        assertThat(runtimeException.getMessage()).isEqualTo("프로필 명은 중복될 수 없습니다.");
    }

    @Test
    void 프로필_필수입력안함() throws Exception {
        // Given
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();

        String saveDtoJsonString = objectMapper.writeValueAsString(profileUpdateRequest);
        System.out.println(saveDtoJsonString);
        // Generate a valid JWT token
        String jwtToken = testGenerateValidJwtToken(); // 유효한 JWT 토큰 생성하는 함수 (구현 필요)
        MvcResult result = mockMvc.perform(put("/api/accounts/test0/profiles")
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
        profileUpdateRequest.setChangeProfileName("수정_kimgun");
        profileUpdateRequest.setProfileIntro("수정_프로필 소개글입니다.");

        accountsService.updateProfile("test0", profileUpdateRequest,null);

        ProfileSearchResponse searchProfile = accountsService.searchProfile("수정_kimgun");
        String profileName = searchProfile.getProfileName();
        String profileIntro = searchProfile.getProfileIntro();

        assertThat(profileName).isEqualTo("수정_kimgun");
        assertThat(profileIntro).isEqualTo("수정_프로필 소개글입니다.");
    }

    @Test
    void 프로필수정_이미지ID있음_파일no(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setChangeProfileName("수정_kimgun");
        profileUpdateRequest.setProfileIntro("수정_프로필 소개글입니다.");

        byte[] fileContent = "Test file content".getBytes();
        MultipartFile file = new MockMultipartFile("profileImg",
                "profile.jpg",
                "image/jpeg",
                fileContent);

        accountsService.updateProfile("test0", profileUpdateRequest, file);

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

    void 프로필수정_존재하지않는회원(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setChangeProfileName("");
        profileUpdateRequest.setProfileIntro("");

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            accountsService.updateProfile("test1234", profileUpdateRequest, null);
        });

        assertThat("유저를 조회할 수 없습니다.").isEqualTo(runtimeException.getMessage());
    }

    @Test
    void 프로필수정_이미지등록(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setChangeProfileName("수정_kimgun");
        profileUpdateRequest.setProfileIntro("수정_프로필 소개글입니다.");

        byte[] fileContent = "Test file content".getBytes();
        MultipartFile file = new MockMultipartFile("profileImg",
                "profile.jpg",
                "image/jpeg",
                fileContent);

        accountsService.updateProfile("test0", profileUpdateRequest, file);

        ProfileSearchResponse searchProfile = accountsService.searchProfile("수정_kimgun");
        String profileName = searchProfile.getProfileName();
        Long profileImgId  = searchProfile.getProfileImgFileId();

        assertThat(profileName).isEqualTo("수정_kimgun");
        assertThat(profileImgId).isNotZero();
    }

    @Test
    void 프로필수정_이미등록된이미지_null입력(){
        Accounts test0 = accountsRepository.findByProfileName("test0").get();
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();

        profileUpdateRequest.setChangeProfileName("");
        profileUpdateRequest.setProfileIntro("");
        profileUpdateRequest.setProfileImgFileId(test0.getProfileImgFileId());

        accountsService.updateProfile("test0", profileUpdateRequest, null);

        ProfileSearchResponse searchProfile = accountsService.searchProfile(test0.getProfileName());
        System.out.println(searchProfile.getProfileImg());
    }

    @Test
    @Rollback(false)
    void 이미지삭제() throws IOException {
        String profileName = "test0";
        String result = accountsService.deleteProfileImage(profileName);
        assertThat("파일이 삭제되었습니다.").isEqualTo(result);
        updateImage(profileName);
    }

    private void updateImage(String profileName) throws IOException {
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        int imgNumber = 0;
        String filePath = "C:/Images/"+"test"+imgNumber+".jpg";
        byte[] fileData = Files.readAllBytes(Paths.get(filePath));

        MockMultipartFile file = new MockMultipartFile(
                "test"+imgNumber,
                "test"+imgNumber+".jpg",
                "image/jpeg",
                fileData
        );

        accountsService.updateProfile(profileName, profileUpdateRequest, file);
    }

    @Test
    void 이미지삭제_파일이없을경우(){
        String profileName = "test0";
        assertThrows(RuntimeException.class, () -> {
            String result = accountsService.deleteProfileImage(profileName);
        });
    }

    @Test
    void 이미지등록_파일이있을경우() throws IOException {
        String profileName = "test4";
        int imgNumber = 1;
        String filePath = "C:/Images/"+"test"+imgNumber+".jpg";
        byte[] fileData = Files.readAllBytes(Paths.get(filePath));

        MockMultipartFile file = new MockMultipartFile(
                "test"+imgNumber,
                "test"+imgNumber+".jpg",
                "image/jpeg",
                fileData
        );

        accountsService.updateProfileImage(profileName, file);
    }

    @Test
    void 프로필_최초가입일(){
        ProfileSignInDayResponse test0 = accountsService.searchProfileSignInDay("test0");
        LocalDateTime createdDate = test0.getCreatedDate();
        String profileImg = test0.getProfileImg();

        System.out.println(profileImg);
        File file = new File(profileImg);
        boolean isFileExists = file.exists();

        assertThat(createdDate).isNotNull();
        assertThat(isFileExists).isEqualTo(true);
    }

    @Test
    void 이메일형식확인(){
        String email = "etkim02@naver.com";
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        assertThat(m.matches()).isEqualTo(true);
    }

    @Test
    void 이메일형식틀림(){
        String email = "etkim02@naver";
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        assertThat(m.matches()).isEqualTo(false);
    }
}