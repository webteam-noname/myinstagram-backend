package com.my.instagram.domains.accounts.service;

import com.my.instagram.common.file.domain.Files;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class AccountsService {
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
        System.out.println("principalDetails ::: " + principalDetails);
        JwtDto jwtDto = jwtProvider.createJwtDto(principalDetails);
        Accounts accounts = accountsRepository.findByUsername(principalDetails.getAccountResponse().getUsername()).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없음"));
        refreshTokenRepository.save(RefreshToken.builder()
                                                .token(jwtDto.getRefreshToken())
                                                .accounts(accounts)
                                                .build());
        return new AccountsLoginResponse(jwtDto, new AccountsResponse(accounts));
    }

    public String signUp(AccountsSaveRequest accountsSaveRequest) {
        // 회원 중복 여부 체크
        usernameOverTwiceExistsException(accountsSaveRequest.getUsername());
        profileNameOverTwiceExistsException(accountsSaveRequest.getProfileName());

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

        return "회원가입에 성공했습니다.";
    }

    public Slice<AccountsResponse> searchSliceRecommendAccounts(int currentPage) {
        Pageable pageable = PageRequest.of(currentPage, 10);
        return accountsRepository.findAllSlice(pageable);
    }

    public List<AccountsResponse> searchAccounts(String searchName) {
        Pageable pageable = PageRequest.of(0, 20);
        return accountsRepository.findByName(searchName+"%", pageable);
    }

    public String updatePassword(AccountsUpdateRequest accountsUpdateRequest) {
        Accounts accounts = getAccounts(accountsUpdateRequest.getProfileName());

        if(mailService.validatePasswordCode(accounts.getUsername(),accountsUpdateRequest.getAuthCode())){
            throw new RuntimeException("인증코드가 틀렸습니다. 다시한번 조회해주세요");
        }else{
            // 인증이 완료되고 난 뒤 기존의 인증 코드를 삭제합니다.
            mailService.deletePasswordCode(accounts.getUsername());
        }

        accountsUpdateRequest.setPassword(getEncode(accountsUpdateRequest.getPassword()));
        accounts.updatePassword(accountsUpdateRequest);

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

    public ProfileUpdateResponse updateProfile(ProfileUpdateRequest profileUpdateRequest, MultipartFile file) {
        profileNameOverTwiceExistsException(profileUpdateRequest.getChangeProfileName());
        Accounts accounts = getAccounts(profileUpdateRequest.getProfileName());

        Long fileId = getFileId(file, profileUpdateRequest.getProfileImgFileId());
        profileUpdateRequest.setProfileImgFileId(fileId);

        // profileNameOverTwiceExistsException(profileUpdateRequest.getProfileName());
        accounts.updateProfile(profileUpdateRequest);
        return new ProfileUpdateResponse(accounts);
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


    private void usernameOverTwiceExistsException(String username) {
        System.out.println(accountsRepository.countByUsername(username));
        if(accountsRepository.countByUsername(username) > 0){
            throw new RuntimeException("사용자 ID는 중복될 수 없습니다.");
        }
    }

    private void profileNameOverTwiceExistsException(String profileName) {
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

    public String updateSignInPassword(AccountsUpdateSignInRequest accountsUpdateSignInRequest) {
        return null;
    }

    public String updateProfilePassword(String profileName, UpdateProfilePasswordRequest updateProfilePasswordRequest) {
        return null;
    }

    public ProfileSignInDayResponse searchProfileSignInDay(String profileName) {
        return null;
    }

    public ProfileUpdateResponse updateProfileImage(ProfileUpdateImageRequest profileUpdateImageRequest, MultipartFile file) {
        return null;
    }

    public String deleteProfileImage(ProfileDeleteImageRequest profileDeleteImageRequest) {
        return null;
    }
}
