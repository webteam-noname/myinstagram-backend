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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
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

    @PostMapping("/api/auth/accounts/temp/sign-in")
    public ApiResponse<AccountsLoginResponse> tempSignIn(@Valid @RequestBody AccountsConfirmRequest accountsConfirmRequest,
                                                         HttpServletResponse response){
        AccountsLoginResponse accountsLoginResponse = accountService.tempSignIn(accountsConfirmRequest);
        setCookie(response,accountsLoginResponse.getJwt());
        return new ApiResponse<>(HttpStatus.OK, accountsLoginResponse);
    }

    @PostMapping("/api/auth/accounts/sign-out")
    public ApiResponse<String> signOut(){
        // 현재 인증 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보를 제거하여 로그아웃 처리
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }

        return new ApiResponse<>(HttpStatus.OK, "로그아웃 처리 완료");
    }

    @PostMapping("/api/auth/accounts/passwords/emails")
    public ApiResponse<String> sendUpdatePasswordEmail(@Valid @RequestBody MailUpdatePasswordRequest mailUpdatePasswordRequest){
        return new ApiResponse<>(HttpStatus.OK, mailService.sendUpdatePasswordEmail(mailUpdatePasswordRequest));
    }

    @GetMapping("/api/auth/accounts/passwords/reset/sign-in/confirmations")
    public ApiResponse<String> confirmEmailSignIn(AccountsConfirmRequest accountsConfirmRequest,
                                                  HttpServletResponse response){
        return new ApiResponse<>(HttpStatus.OK, accountService.confirmEmailSignIn(accountsConfirmRequest,response));
    }

    @GetMapping("/api/auth/accounts/passwords/reset/confirmations")
    public ApiResponse<String> confirmEmailPassword(AccountsConfirmRequest accountsConfirmRequest,
                                                  HttpServletResponse response){
        return new ApiResponse<>(HttpStatus.OK, accountService.confirmEmailPassword(accountsConfirmRequest,response));
    }

    @PutMapping("/api/accounts/passwords/reset")
    public ApiResponse<String> updatePassword(@Valid @RequestBody AccountsUpdatePasswordRequest accountsUpdatePasswordRequest){
        return new ApiResponse<>(HttpStatus.OK, accountService.updatePassword(accountsUpdatePasswordRequest));
    }

    @PutMapping("/api/accounts/{profileName}/passwords/reset")
    public ApiResponse<String> updateProfilePassword(@PathVariable("profileName") String profileName,
                                                     @Valid @RequestBody UpdateProfilePasswordRequest updateProfilePasswordRequest){
        return new ApiResponse<>(HttpStatus.OK, accountService.updateProfilePassword(profileName, updateProfilePasswordRequest));
    }

    @GetMapping("/api/accounts/{profileName}/profiles/sign-in-days")
    public ApiResponse<ProfileSignInDayResponse> searchProfileSignInDay(@PathVariable("profileName") String profileName){
        ProfileSignInDayResponse accountsLoginResponse = accountService.searchProfileSignInDay(profileName);
        return new ApiResponse<>(HttpStatus.OK, accountsLoginResponse);
    }

    @GetMapping("/api/accounts/{profileName}/profiles")
    public ApiResponse<ProfileSearchResponse> searchProfile(@PathVariable("profileName") String profileName){
        ProfileSearchResponse accountsLoginResponse = accountService.searchProfile(profileName);
        return new ApiResponse<>(HttpStatus.OK, accountsLoginResponse);
    }

    @PutMapping("/api/accounts/{profileName}/images")
    public ApiResponse<String> updateProfileImage(@PathVariable("profileName") String profileName,
                                                  MultipartFile file){
        return new ApiResponse<>(HttpStatus.OK, accountService.updateProfileImage(profileName, file));
    }

    @DeleteMapping("/api/accounts/{profileName}/images")
    public ApiResponse<String> deleteProfileImage(@PathVariable("profileName") String profileName){
        return new ApiResponse<>(HttpStatus.OK, accountService.deleteProfileImage(profileName));
    }

    @PutMapping("/api/accounts/{profileName}/profiles")
    public ApiResponse<AccountsLoginResponse> updateProfile(@PathVariable("profileName") String profileName,
                                                            @Valid @RequestBody ProfileUpdateRequest profileUpdateRequest,
                                                            MultipartFile file,
                                                            HttpServletResponse response){
        AccountsLoginResponse profileUpdateResponse = accountService.updateProfile(profileName, profileUpdateRequest,file);
        setCookie(response,profileUpdateResponse.getJwt());
        return new ApiResponse<>(HttpStatus.OK, profileUpdateResponse);
    }

    @GetMapping("/api/accounts/recommendations/pages/{currentPage}")
    public ApiResponse<Slice<AccountsResponse>> searchSliceRecommendAccounts(@PathVariable("currentPage") int currentPage) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, accountService.searchSliceRecommendAccounts(currentPage));
    }

    @GetMapping("/api/accounts/{searchName}")
    public ApiResponse<List<AccountsResponse>> searchAccounts(@PathVariable("searchName") String searchName) throws IOException {
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
