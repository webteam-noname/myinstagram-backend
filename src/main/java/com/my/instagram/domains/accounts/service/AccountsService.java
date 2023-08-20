package com.my.instagram.domains.accounts.service;

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

    public List<ProfileSearchResponse> searchAccounts(String searchName) {
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
        Accounts accounts = getAccountsWithFile(profileName);
        return new ProfileSearchResponse(accounts);
    }

    // 2023-08-14 프로필을 조회
    // 기존의 조회는 fileRepository에서 file을 조회한 후 그 파일을 다시 Dto에 넣는 방식이었다.
    // 흠... 진짜 이상하다.. ㅋㅋㅋ
    // 그래서 현재는 getAccountsWithFile에서 파일 조회한 뒤 DTO에서 후속 처리를 하였다.
    // 그것조차 아직까지는 문제가 있기에 조금더 수정을 해야할 것 같다.
    public ProfileSearchResponse searchProfile1(String profileName) {
        Accounts accounts = getAccountsWithFile(profileName);
        return new ProfileSearchResponse(accounts);
    }

    // 2023-08-14 프로필을 업데이트
    // 프로필을 업데이트 하는 것에서 파일 저장 영역을 Accounts으로 밀어넣으려했다.
    // 하지만 그렇게 작업하는 것은 Accounts Entity의 역할을 다하지 못하게 만든다는 생각이 들었다.
    // 그래서 Service에서 파일을 수정하고 삭제하는 퍼블릭 인터페이스를 만들었다.
    // 프로필 수정을 saveProfile1과 updateProfile1으로 만들려했다.
    // 하지만 점점 만들다보니 프로필의 경우 회원가입시 저장되고, 이후 개인 설정에서의 프로필은 수정된다.
    // 결국 저장이 아닌 수정이기에 updateProfile1이라고 표현했다.
    // 하지만 Account 안에 있는 Files가 null인지의 여부에 따라 파일을 저장하거나 업데이트하는게 정말
    // 최선의 방법일까라는 생각이 든다.
    // 결론적으로 분기를 통해서가 아닌 하나의 메서드로만 호출하고 싶은데 뭔가 의미가 모호해진다는 생각에서
    // 더 좋은 방법이 있지않을까하는 생각이 들었다.
    // 하지만 너무 오랜시간 고민을 하다보니 답이 안나와 일단 지금 내가 생각할 수 있는 최선의 답을 적은 것 같다.
    public void updateProfile1(String profileName, ProfileUpdateRequest profileUpdateRequest, MultipartFile file) {
        Accounts accounts = getAccountsWithFile(profileName);
        accounts.updateProfile(profileUpdateRequest);

        if(accounts.getFiles() == null){
            fileService.saveSingleFile(accounts,file);
        }else{
            fileService.updateSingleFile(accounts.getFiles(), file);
        }
    }
    // 2023-08-14 회원정보 파일과 함께 조회
    // @EntityGraph를 사용해 Accounts와 Files를 조회하였다.
    // 너무 편하다... ㅋㅋㅋㅋ
    private Accounts getAccountsWithFile(String profileName) {
        return accountsRepository.findWithFilesByProfileName(profileName).get();
    }

    // 2023-08-12 파일 적용방식 변경
    // 업데이트 및 저장을 할 경우 return 값이 없도록 메서드를 만드는게 맞는데 ..
    // 그 당시엔 프로젝트를 빨리 끝내야한다는 압박때문에 좀 급하게 만드느라 업데이트 후 리턴값을 가지게 되었다..
    // 업데이트는 하는 값엔 리턴값을 가지지 않도록 해야겠다.
    // 또한 기존에 파일을 조회하던 메서드인 getFileId를 제거하였다.
    // 기존 getFileId 메서드도 return과 수정을 동시에 하는 코드였다.. 흠.. 그래서 삭제를 하였다.

    // 그렇지만 당장은 해당 메서드를 삭제할 순 없었다.. 왜냐하면 프론트에서도 이 메서드를 사용하기 때문이다...ㅠ
    // 무튼 추후에 없어져야할 메서드의 형태이다..
    // 추후 삭제를 하면서 기존에 잘못 적용된 파일 처리 방식을 삭제해야겠다.
    public AccountsLoginResponse updateProfile(String profileName, ProfileUpdateRequest profileUpdateRequest, MultipartFile file) {
        profileNameOverTwiceExistsException(profileUpdateRequest.getChangeProfileName());

        // 2023-08-15 소스 변경
        // 기존의 getAccount 메서드 호출을 getAccountsWithFile로 변경하였다.
        Accounts accounts = getAccountsWithFile(profileName);
        accounts.updateProfile(profileUpdateRequest);

        if(accounts.getFiles() == null){
            fileService.saveSingleFile(accounts,file);
        }else{
            fileService.updateSingleFile(accounts.getFiles(), file);
        }

        // 급하게 만들었지만 아래와 같은 방법은 지양되어야 한다.
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

    // 2023-08-17 당장은 사용되지 않아 주석처리
    /*public void usernameOverTwiceExistsException(String username) {
        if(accountsRepository.countByUsername(username) > 0){
            throw new RuntimeException("사용자 ID는 중복될 수 없습니다.");
        }
    }*/

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
        Accounts accounts = getAccountsWithFile(profileName);
        return new ProfileSignInDayResponse(accounts);
    }

    public void updateProfileImage(String profileName, MultipartFile file) {
        Accounts accounts = getAccountsWithFile(profileName);
        accounts.getFiles().updateSingleFile(file);
    }

    public void deleteProfileImage(String profileName) {
        Accounts accounts = getAccountsWithFile(profileName);
        accounts.getFiles().deleteSingleFile();
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
