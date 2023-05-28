package com.my.instagram.domains.accounts.service;

import com.my.instagram.config.security.auth.PrincipalDetails;
import com.my.instagram.config.security.jwt.JwtProvider;
import com.my.instagram.config.security.jwt.dto.JwtDto;
import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.accounts.domain.AccountsRole;
import com.my.instagram.domains.accounts.domain.RefreshToken;
import com.my.instagram.domains.accounts.domain.Role;
import com.my.instagram.domains.accounts.dto.request.*;
import com.my.instagram.domains.accounts.dto.response.AccountsLoginResponse;
import com.my.instagram.domains.accounts.dto.response.AccountsResponse;
import com.my.instagram.domains.accounts.dto.response.ProfileSearchResponse;
import com.my.instagram.domains.accounts.repository.AccountsRepository;
import com.my.instagram.domains.accounts.repository.AccountsRolesRepository;
import com.my.instagram.domains.accounts.repository.RefreshTokenRepository;
import com.my.instagram.domains.accounts.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountsService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountsRepository accountsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountsRolesRepository accountsRolesRepository;
    private final MailService mailService;
    private final RoleRepository roleRepository;
    private final JwtProvider jwtProvider;

    public AccountsLoginResponse login(AccountsLoginReqeust accountsLoginReqeust){
        PrincipalDetails principalDetails = (PrincipalDetails) getAuthentication(accountsLoginReqeust).getPrincipal();
        JwtDto jwtDto = jwtProvider.createJwtDto(principalDetails);
        Accounts accounts = accountsRepository.findByUsername(principalDetails.getAccountResponse().getUsername()).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없음"));
        refreshTokenRepository.save(RefreshToken.builder()
                                                .token(jwtDto.getRefreshToken())
                                                .accounts(accounts)
                                                .build());
        return new AccountsLoginResponse(jwtDto, new AccountsResponse(accounts));
    }

    public String join(AccountsSaveRequest accountsSaveRequest) {
        // 회원 중복 여부 체크
        // validationAccounts();
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

    public String updatePassword(AccountsUpdateRequest accountsUpdateRequest) {
        Accounts accounts = getAccounts(accountsUpdateRequest.getUsername());

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

    public ProfileSearchResponse searchProfile(ProfileSearchRequest profileSearchRequest) {
        Accounts accounts = getAccounts(profileSearchRequest.getUsername());

        return new ProfileSearchResponse(accounts);
    }

    public Long updateProfie(ProfileUpdateRequest profileUpdateRequest) {
        Accounts accounts = getAccounts(profileUpdateRequest.getUsername());

        // 프로필을 수정합니다.
        accounts.updateProfile(profileUpdateRequest);

        return accounts.getId();
    }

    private Accounts getAccounts(String username) {
        Accounts accounts = accountsRepository.findByUsername(username)
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
}
