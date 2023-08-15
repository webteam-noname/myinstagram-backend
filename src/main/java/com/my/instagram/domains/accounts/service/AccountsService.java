package com.my.instagram.domains.accounts.service;

import com.my.instagram.common.file.domain.Files;
import com.my.instagram.common.file.dto.request.FileDeleteRequest;
import com.my.instagram.common.file.dto.request.FileUpdateRequest;
import com.my.instagram.common.file.repository.FileRepository;
import com.my.instagram.common.file.service.FileService;
import com.my.instagram.config.security.auth.PrincipalDetails;
import com.my.instagram.config.security.jwt.JwtProvider;
import com.my.instagram.config.security.jwt.dto.JwtDto;
import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.accounts.domain.AccountsRole;
import com.my.instagram.domains.accounts.domain.RefreshToken;
import com.my.instagram.domains.accounts.domain.Role;
import com.my.instagram.domains.accounts.dto.request.*;
import com.my.instagram.domains.accounts.dto.response.*;
import com.my.instagram.domains.accounts.repository.AccountsRepository;
import com.my.instagram.domains.accounts.repository.AccountsRolesRepository;
import com.my.instagram.domains.accounts.repository.RefreshTokenRepository;
import com.my.instagram.domains.accounts.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class AccountsService extends EmailLogin{

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountsRepository accountsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountsRolesRepository accountsRolesRepository;
    private final MailService mailService;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final RoleRepository roleRepository;
    private final JwtProvider jwtProvider;

    public AccountsLoginResponse login(AccountsLoginReqeust accountsLoginReqeust){
        PrincipalDetails principalDetails = (PrincipalDetails) getAuthentication(accountsLoginReqeust).getPrincipal();
        JwtDto jwtDto = jwtProvider.createJwtDto(principalDetails);
        Accounts accounts = getAccountsByUsername(principalDetails.getAccountResponse().getUsername());
        refreshTokenRepository.save(RefreshToken.builder()
                                                .token(jwtDto.getRefreshToken())
                                                .accounts(accounts)
                                                .build());

        return new AccountsLoginResponse(jwtDto, new AccountsResponse(accounts));
    }

    public AccountsLoginResponse tempSignIn(AccountsConfirmRequest accountsConfirmRequest) {
        String username = getEamilLoginRealUsername(accountsConfirmRequest.getUidb());
        AccountsSearchResponse accountResponse = accountsRepository.findByUsernameInQuery(username).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없음"));
        PrincipalDetails principalDetails = new PrincipalDetails(accountResponse);

        JwtDto jwtDto = jwtProvider.createJwtDto(principalDetails);
        Accounts selectAccounts = getAccountsByUsername(principalDetails.getAccountResponse().getUsername());

        refreshTokenRepository.save(RefreshToken.builder()
                .token(jwtDto.getRefreshToken())
                .accounts(selectAccounts)
                .build());

        return new AccountsLoginResponse(jwtDto, new AccountsResponse(selectAccounts));
    }

    private Accounts getAccountsByUsername(String username) {
        Accounts accounts = accountsRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없음"));
        return accounts;
    }

    public String signUp(AccountsSaveRequest accountsSaveRequest) {
        AccountsCodeRequest accountsCodeRequest = new AccountsCodeRequest();
        accountsCodeRequest.setAuthCode(accountsSaveRequest.getAuthCode());
        accountsCodeRequest.setUsername(accountsSaveRequest.getUsername());
        inputJoinCodeEmail(accountsCodeRequest);

        Accounts accounts = Accounts.builder()
                                    .username(accountsSaveRequest.getUsername())
                                    .name(accountsSaveRequest.getName())
                                    .profileName(accountsSaveRequest.getProfileName())
                                    .password(getEncode(accountsSaveRequest.getPassword()))
                                    .build();

        Role roleAccounts = roleRepository.findByType("ROLE_USER").get();

        AccountsRole accountsRole = AccountsRole.builder()
                                                .accounts(accounts)
                                                .role(roleAccounts)
                                                .build();

        accountsRepository.save(accounts);
        accountsRolesRepository.save(accountsRole);

        return "회원가입을 완료했습니다.";
    }

    public void inputJoinCodeEmail(AccountsCodeRequest accountsCodeRequest) {
        if(mailService.validateJoinCode(accountsCodeRequest.getUsername(),accountsCodeRequest.getAuthCode())){
            throw new RuntimeException("인증코드가 틀렸습니다. 다시한번 조회해주세요");
        }else{
            mailService.deletePasswordCode(accountsCodeRequest.getUsername());
        }
    }

    public Slice<AccountsResponse> searchSliceRecommendAccounts(int currentPage) {
        Pageable pageable = PageRequest.of(currentPage, 10);
        return accountsRepository.findAllSlice(pageable);
    }

    public List<AccountsResponse> searchAccounts(String searchName) {
        Pageable pageable = PageRequest.of(0, 20);
        return accountsRepository.findByName(searchName+"%", pageable);
    }

    public String updatePassword(AccountsUpdatePasswordRequest accountsUpdatePasswordRequest) {
        String uidb = accountsUpdatePasswordRequest.getUidb();
        String username = getEamilLoginRealUsername(uidb);
        Accounts accounts = getAccountsByUsername(username);

        String password = accountsUpdatePasswordRequest.getPassword();
        String checkPassword = accountsUpdatePasswordRequest.getCheckPassword();

        if(!password.equals(checkPassword)){
            throw new RuntimeException("변경할 비밀번호가 일치하지 않습니다.");
        }

        accounts.updatePassword(getEncode(accountsUpdatePasswordRequest.getPassword()));
        deleteTempAccounts(uidb);


        return "비밀번호가 변경되었습니다.";
    }

    public String updateProfilePassword(String profileName, UpdateProfilePasswordRequest updateProfilePasswordRequest) {
        Accounts accounts = getAccounts(profileName);
        String password = updateProfilePasswordRequest.getPassword();
        String checkPassword = updateProfilePasswordRequest.getCheckPassword();

        if(!password.equals(checkPassword)){
            throw new RuntimeException("변경할 비밀번호가 일치하지 않습니다.");
        }

        accounts.updatePassword(getEncode(accounts.getPassword()));
        return "비밀번호가 변경되었습니다.";
    }

    public ProfileSearchResponse searchProfile(String profileName) {
        Accounts accounts = getAccounts(profileName);
        Files file = null;

        if(accounts.getProfileImgFileId() != null){
            file = fileRepository.findById(accounts.getProfileImgFileId()).get();
        }

        return new ProfileSearchResponse(accounts, file);
    }



    public void updateProfileTest(String profileName, ProfileUpdateRequest profileUpdateRequest, MultipartFile file) {
        Accounts accounts = getAccounts(profileName);
        // accounts.updateProfileTest(profileUpdateRequest);
        fileService.saveFileTest(accounts, file);
    }

    // 2023-08-12 파일 적용방식 변경
    //
    public AccountsLoginResponse updateProfile(String profileName, ProfileUpdateRequest profileUpdateRequest, MultipartFile file) {
        System.out.println("프로필 업데이트");
        profileNameOverTwiceExistsException(profileUpdateRequest.getChangeProfileName());
        Accounts accounts = getAccounts(profileName);

        Long fileId = null;

        if(file != null){
            fileId = getFileId(file, profileUpdateRequest.getProfileImgFileId());
            profileUpdateRequest.setProfileImgFileId(fileId);
        }

        // profileNameOverTwiceExistsException(profileUpdateRequest.getProfileName());
        accounts.updateProfile(profileUpdateRequest);

        AccountsSearchResponse accountResponse = accountsRepository.findByUsernameInQuery(accounts.getUsername()).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없음"));
        PrincipalDetails principalDetails = new PrincipalDetails(accountResponse);

        JwtDto jwtDto = jwtProvider.createJwtDto(principalDetails);
        Accounts selectAccounts = getAccountsByUsername(principalDetails.getAccountResponse().getUsername());

        refreshTokenRepository.save(RefreshToken.builder()
                              .token(jwtDto.getRefreshToken())
                              .accounts(selectAccounts)
                              .build());

        return new AccountsLoginResponse(jwtDto, new AccountsResponse(selectAccounts));
    }

    private Long getFileId(MultipartFile file, Long imgFileId) {

        // 프로필을 수정합니다.
        if(imgFileId == null){
            // 이미지 파일이 존재하지 않으면
            imgFileId = fileService.saveFile(file);
        }else{
            // 이미지 파일이 존재하면
            imgFileId = fileService.updateFile(new FileUpdateRequest(imgFileId), file);
        }

        return imgFileId;
    }


    public void usernameOverTwiceExistsException(String username) {
        if(accountsRepository.countByUsername(username) > 0){
            throw new RuntimeException("사용자 ID는 중복될 수 없습니다.");
        }
    }

    public void profileNameOverTwiceExistsException(String profileName) {
        System.out.println(accountsRepository.countByProfileName(profileName));
        if(accountsRepository.countByProfileName(profileName) > 0){
            throw new RuntimeException("프로필 명은 중복될 수 없습니다.");
        }
    }

    private Accounts getAccounts(String profileName) {
        Accounts accounts = accountsRepository.findByProfileName(profileName)
                                              .orElseThrow(() -> new RuntimeException("유저를 조회할 수 없습니다."));

        return accounts;
    }

    private String getEncode(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private Authentication getAuthentication(AccountsLoginReqeust loginReqeust) {
        return authenticationManagerBuilder.getObject()
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginReqeust.getUsername(),
                        loginReqeust.getPassword())
                );
    }


    public ProfileSignInDayResponse searchProfileSignInDay(String profileName) {
        Accounts accounts = getAccounts(profileName);
        Files file = null;

        if(accounts.getProfileImgFileId() != null){
            file = fileRepository.findById(accounts.getProfileImgFileId()).get();
        }

        return new ProfileSignInDayResponse(accounts, file);
    }

    public String updateProfileImage(String profileName, MultipartFile file) {
        Accounts accounts = getAccounts(profileName);
        fileService.updateFile(new FileUpdateRequest(accounts.getProfileImgFileId()), file);
        return "업데이트가 완료되었습니다.";
    }

    public String deleteProfileImage(String profileName) {
        Accounts accounts = getAccounts(profileName);
        fileService.deleteFile(new FileDeleteRequest(accounts.getProfileImgFileId()));
        accounts.clearFileImgId();
        return "파일이 삭제되었습니다.";
    }

    public String confirmEmailSignIn(AccountsConfirmRequest accountsConfirmRequest,
                                     HttpServletResponse response) {
        String uidb = accountsConfirmRequest.getUidb();
        String accessToken = accountsConfirmRequest.getAccessToken();

        isAutoCountOverFirstExistsException(uidb);
        increaseAutoCount(uidb);

        try {
            response.sendRedirect("http://10.40.1.129:8080/accounts/recoveryPassword?uidb="+uidb+"&accessToken="+accessToken);
        } catch (IOException e) {
            throw new RuntimeException("Vue 서버를 확인해주세요!");
        }


        return "정상 처리 되었습니다.";
    }

    public String confirmEmailPassword(AccountsConfirmRequest accountsConfirmRequest, HttpServletResponse response) {
        String uidb = accountsConfirmRequest.getUidb();
        String accessToken = accountsConfirmRequest.getAccessToken();

        if(isRightTempAccessToken(uidb, accessToken)){

            try {
                response.sendRedirect("http://10.40.1.129:8080/accounts/changePassword?uidb="+uidb+"&accessToken="+accessToken);
            } catch (IOException e) {
                throw new RuntimeException("Vue 서버를 확인해주세요!");
            }
        }else{
            throw new RuntimeException("올바르지 않은 토큰입니다.");
        }

        return "정상 처리 되었습니다.";
    }


}
