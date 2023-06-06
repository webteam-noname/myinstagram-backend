package com.my.instagram.domains.accounts.controller;

import com.my.instagram.domains.accounts.dto.response.MailCodeResponse;
import com.my.instagram.domains.accounts.dto.response.ProfileUpdateResponse;
import com.my.instagram.domains.accounts.service.MailService;
import com.my.instagram.domains.accounts.dto.request.*;
import com.my.instagram.domains.accounts.dto.response.AccountsLoginResponse;
import com.my.instagram.domains.accounts.dto.response.ProfileSearchResponse;
import com.my.instagram.domains.accounts.service.AccountsService;
import com.my.instagram.common.dto.ApiResponse;
import com.my.instagram.config.security.jwt.dto.JwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Transactional
public class AccountsController {

    private final AccountsService accountService;
    private final MailService mailService;


    @PostMapping("/api/auth/accounts/join")
    public ApiResponse<String> join(@Valid @RequestBody AccountsSaveRequest accountsSaveRequest){
        return new ApiResponse<>(HttpStatus.OK, accountService.join(accountsSaveRequest));
    }

    @PostMapping("/api/auth/accounts/login")
    public ApiResponse<AccountsLoginResponse> login(@Valid @RequestBody AccountsLoginReqeust accountsLoginReqeust,
                                                    HttpServletResponse response){
        AccountsLoginResponse accountsLoginResponse = accountService.login(accountsLoginReqeust);
        setCookie(response,accountsLoginResponse.getJwt());
        return new ApiResponse<>(HttpStatus.OK, accountsLoginResponse);
    }

    @GetMapping("/login/oauth2/code/google")
    public void socialCallBack() throws IOException {
        System.out.println("callback");
    }

    @PostMapping("/api/auth/accounts/password/code")
    public ApiResponse<MailCodeResponse> searchPasswordCode(@Valid @RequestBody MailCodeRequest mailCodeRequest) throws Exception {
        return new ApiResponse<>(HttpStatus.OK, mailService.sendPasswordCodeEmail(mailCodeRequest));
    }

    @PutMapping("/api/auth/accounts/password")
    public ApiResponse<String> updatePassword(@Valid @RequestBody AccountsUpdateRequest accountsUpdateRequest){
        return new ApiResponse<>(HttpStatus.OK, accountService.updatePassword(accountsUpdateRequest));
    }

    @GetMapping("/api/accounts/profile")
    public ApiResponse<ProfileSearchResponse> searchProfile(@Valid @RequestBody ProfileSearchRequest profileSearchRequest){
        ProfileSearchResponse accountsLoginResponse = accountService.searchProfile(profileSearchRequest);
        return new ApiResponse<>(HttpStatus.OK, accountsLoginResponse);
    }

    @PutMapping("/api/accounts/profile")
    public ApiResponse<ProfileUpdateResponse> updateProfie(@Valid ProfileUpdateRequest profileUpdateRequest,
                                                           MultipartFile file){
        ProfileUpdateResponse profileUpdateResponse = accountService.updateProfie(profileUpdateRequest,file);
        return new ApiResponse<>(HttpStatus.OK, profileUpdateResponse);
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
