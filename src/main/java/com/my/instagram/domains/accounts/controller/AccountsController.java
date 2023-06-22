package com.my.instagram.domains.accounts.controller;

import com.my.instagram.domains.accounts.dto.response.*;
import com.my.instagram.domains.accounts.service.MailService;
import com.my.instagram.domains.accounts.dto.request.*;
import com.my.instagram.domains.accounts.service.AccountsService;
import com.my.instagram.common.dto.ApiResponse;
import com.my.instagram.config.security.jwt.dto.JwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Transactional
public class AccountsController {

    private final AccountsService accountService;
    private final MailService mailService;

    @PostMapping("/api/auth/accounts/sign-up")
    public ApiResponse<String> signUp(@Valid @RequestBody AccountsSaveRequest accountsSaveRequest){
        return new ApiResponse<>(HttpStatus.OK, accountService.signUp(accountsSaveRequest));
    }

    @PostMapping("/api/auth/accounts/sign-up/codes")
    public ApiResponse<MailCodeResponse> sendJoinCodeEmail(@Valid @RequestBody MailCodeRequest mailCodeRequest) throws Exception {
        return new ApiResponse<>(HttpStatus.OK, mailService.sendJoinCodeEmail(mailCodeRequest));
    }

    @PostMapping("/api/auth/accounts/sign-in")
    public ApiResponse<AccountsLoginResponse> signIn(@Valid @RequestBody AccountsLoginReqeust accountsLoginReqeust,
                                                    HttpServletResponse response){
        AccountsLoginResponse accountsLoginResponse = accountService.login(accountsLoginReqeust);
        setCookie(response,accountsLoginResponse.getJwt());
        return new ApiResponse<>(HttpStatus.OK, accountsLoginResponse);
    }

    @PostMapping("/api/auth/accounts/sign-out")
    public ApiResponse<String> signOut(@Valid @RequestBody AccountsLoginOutReqeust AccountsLoginOutReqeust,
                                       HttpServletResponse response){
        return new ApiResponse<>(HttpStatus.OK, "로그아웃 처리 필요");
    }

    @PostMapping("/api/auth/accounts/password/email")
    public ApiResponse<String> sendUpdatePasswordEmail(@Valid @RequestBody MailUpdatePasswordRequest mailUpdatePasswordRequest){
        return new ApiResponse<>(HttpStatus.OK, mailService.sendUpdatePasswordEmail(mailUpdatePasswordRequest));
    }

    @PutMapping("/api/auth/accounts/sign-in/password")
    public ApiResponse<String> updateSignInPassword(@Valid @RequestBody AccountsUpdateSignInRequest accountsUpdateSignInRequest){
        return new ApiResponse<>(HttpStatus.OK, accountService.updateSignInPassword(accountsUpdateSignInRequest));
    }

    @PutMapping("/api/auth/accounts/password")
    public ApiResponse<String> updatePassword(@Valid @RequestBody AccountsUpdateRequest accountsUpdateRequest){
        return new ApiResponse<>(HttpStatus.OK, accountService.updatePassword(accountsUpdateRequest));
    }

    @PutMapping("/api/auth/accounts/{profile-name}/password")
    public ApiResponse<String> updateProfilePassword(@PathVariable("profileName") String profileName,
                                                     @Valid @RequestBody UpdateProfilePasswordRequest updateProfilePasswordRequest){
        return new ApiResponse<>(HttpStatus.OK, accountService.updateProfilePassword(profileName, updateProfilePasswordRequest));
    }

    @GetMapping("/api/accounts/{profile-name}/profile/sign-in-day")
    public ApiResponse<ProfileSignInDayResponse> searchProfileSignInDay(@PathVariable("profileName") String profileName){
        ProfileSignInDayResponse accountsLoginResponse = accountService.searchProfileSignInDay(profileName);
        return new ApiResponse<>(HttpStatus.OK, accountsLoginResponse);
    }

    @GetMapping("/api/accounts/{profile-name}/profile")
    public ApiResponse<ProfileSearchResponse> searchProfile(@PathVariable("profileName") String profileName){
        ProfileSearchResponse accountsLoginResponse = accountService.searchProfile(profileName);
        return new ApiResponse<>(HttpStatus.OK, accountsLoginResponse);
    }

    @PutMapping("/api/accounts/{profile-name}/image")
    public ApiResponse<ProfileUpdateResponse> updateProfileImage(@Valid @RequestBody ProfileUpdateImageRequest profileUpdateImageRequest,
                                                                 MultipartFile file){
        ProfileUpdateResponse profileUpdateResponse = accountService.updateProfileImage(profileUpdateImageRequest, file);
        return new ApiResponse<>(HttpStatus.OK, profileUpdateResponse);
    }

    @DeleteMapping("/api/accounts/{profile-name}/image")
    public ApiResponse<String> deleteProfileImage(@Valid @RequestBody ProfileDeleteImageRequest profileDeleteImageRequest,
                                                                 MultipartFile file){
        return new ApiResponse<>(HttpStatus.OK, accountService.deleteProfileImage(profileDeleteImageRequest));
    }

    @PutMapping("/api/accounts/{profile-name}/profile")
    public ApiResponse<ProfileUpdateResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest profileUpdateRequest,
                                                            MultipartFile file){
        ProfileUpdateResponse profileUpdateResponse = accountService.updateProfile(profileUpdateRequest,file);
        return new ApiResponse<>(HttpStatus.OK, profileUpdateResponse);
    }

    @GetMapping("/api/accounts/recommendation/page/{current-page}")
    public ApiResponse<Slice<AccountsResponse>> searchSliceRecommendAccounts(@PathVariable("current-page") int currentPage) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, accountService.searchSliceRecommendAccounts(currentPage));
    }

    @GetMapping("/api/accounts/{search-name}")
    public ApiResponse<List<AccountsResponse>> searchAccounts(@PathVariable("search-name") String searchName) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, accountService.searchAccounts(searchName));
    }

    private void setCookie(HttpServletResponse response, JwtDto jwtDto) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", jwtDto.getRefreshToken())
                .sameSite("None")
                //                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
