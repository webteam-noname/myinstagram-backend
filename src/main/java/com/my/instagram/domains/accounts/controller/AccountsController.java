package com.my.instagram.domains.accounts.controller;

import com.my.instagram.domains.accounts.dto.request.*;
import com.my.instagram.domains.accounts.dto.response.AccountsLoginResponse;
import com.my.instagram.domains.accounts.dto.response.ProfileSearchResponse;
import com.my.instagram.domains.accounts.service.AccountsService;
import com.my.instagram.common.dto.ApiResponse;
import com.my.instagram.config.security.jwt.dto.JwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AccountsController {

    private final AccountsService accountService;

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

    @GetMapping("/api/auth/accounts/password")
    public ApiResponse<String> searchPassword(@Valid @RequestBody AccountsSaveRequest accountsSaveRequest){
        // 이메일 전송 구현 필요
        String check = "구현 필요";
        return new ApiResponse<>(HttpStatus.OK, check);
    }

    @PutMapping("/api/auth/accounts/password/{username}")
    public ApiResponse<Long> updatePassword(@Valid @RequestBody AccountsUpdateRequest accountsUpdateRequest){
        Long accountId = accountService.updatePassword(accountsUpdateRequest);
        return new ApiResponse<>(HttpStatus.OK, accountId);
    }

    @GetMapping("/api/accounts/{username}/profile")
    public ApiResponse<ProfileSearchResponse> searchProfile(@Valid @RequestBody ProfileSearchRequest profileSearchRequest){
        ProfileSearchResponse accountsLoginResponse = accountService.searchProfile(profileSearchRequest);
        return new ApiResponse<>(HttpStatus.OK, accountsLoginResponse);
    }

    @PutMapping("/api/accounts/{username}/profile")
    public ApiResponse<Long> updateProfie(@Valid @RequestBody ProfileUpdateRequest profileUpdateRequest){
        Long accountId = accountService.updateProfie(profileUpdateRequest);
        return new ApiResponse<>(HttpStatus.OK, accountId);
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
