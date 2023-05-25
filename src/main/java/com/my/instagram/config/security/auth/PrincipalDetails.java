package com.my.instagram.config.security.auth;

// 시큐리티가 /login 주소 요청이오면 낚아채서 로그인을 진행시킨다.
// 로그인 진행이 완료가 되면 시큐리티 session을 만들어줍니다.(Security ContextHolder)
// 오브젝트 => Authentication 타입 객체
// Authentication안에 Accounts정보가 있어야 함
// Accounts 오브젝트 타입 =>

// Security Session => Authentication => UserDetails

import com.my.instagram.domains.accounts.domain.AccountsRole;
import com.my.instagram.domains.accounts.dto.response.AccountsSearchResponse;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private final AccountsSearchResponse accountResponse; // 콤포지션
    private Map<String, Object> attributes;

    // 일반 로그인
    public PrincipalDetails(AccountsSearchResponse accountResponse){
        this.accountResponse     = accountResponse;
    }

    // OAuth 로그인
    public PrincipalDetails(AccountsSearchResponse accountResponse,
                            Map<String, Object> attributes){
        this.accountResponse     = accountResponse;
        this.attributes          = attributes;
    }

    // 해당 Accounts의 권한을 리턴하는 곳!!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (AccountsRole accountsRole : accountResponse.getAccountsRoles()) {
            authorities.add((GrantedAuthority) () -> accountsRole.getRole().getType());
        }

        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return accountResponse.getName();
    }

    @Override
    public String getPassword() {
        return accountResponse.getPassword();
    }

    @Override
    public String getUsername() {
        return accountResponse.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {

        // 우리 사이트에서 1년 동안 회원이 로그인을 하지 않았다면
        // 현재 시간 - 로그시간 => 1년 초과하면 return false;

        return true;
    }

}
