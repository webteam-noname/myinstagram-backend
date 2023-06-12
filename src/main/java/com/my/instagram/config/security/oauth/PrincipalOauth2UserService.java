package com.my.instagram.config.security.oauth;

import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.accounts.domain.AccountsRole;
import com.my.instagram.domains.accounts.domain.Role;
import com.my.instagram.domains.accounts.dto.response.AccountsSearchResponse;
import com.my.instagram.domains.accounts.repository.AccountsRepository;
import com.my.instagram.domains.accounts.repository.AccountsRolesRepository;
import com.my.instagram.domains.accounts.repository.RoleRepository;
import com.my.instagram.config.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>  {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountsRepository accountsRepository;
    private final RoleRepository roleRepository;
    private final AccountsRolesRepository accountsRolesRepository;
    private final HttpSession httpSession;

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String provider   = userRequest.getClientRegistration().getClientId(); // google
        String providerId = oAuth2User.getAttribute("sub");
        String username   = oAuth2User.getAttribute("email");

        AccountsSearchResponse searchResponse = accountsRepository.findByUsernameInQuery(username).orElse(null);

        if(searchResponse == null){
            searchResponse = createAccounts(oAuth2User, provider, providerId, username);
        }

        // 회원 가입을 강제로 진행
        return new PrincipalDetails(searchResponse, oAuth2User.getAttributes());
    }

    private AccountsSearchResponse createAccounts(OAuth2User oAuth2User, String provider, String providerId,String username) {
        String password   = bCryptPasswordEncoder.encode("a@#ad5bwsda!$23");
        String email      = oAuth2User.getAttribute("email");
        String name       = oAuth2User.getAttribute("name");

        Accounts accounts = Accounts.builder()
                                    .username(username)
                                    .name(name)
                                    .password(password)
                                    .provider(provider)
                                    .providerId(providerId)
                                    .profileName(name) // 수정이 필요해 보임
                                    .build();

        Role roleAccounts = roleRepository.findByType("ROLE_USER").get();

        AccountsRole accountsRole = AccountsRole.builder()
                                                .accounts(accounts)
                                                .role(roleAccounts)
                                                .build();

        accountsRepository.save(accounts);
        accountsRolesRepository.save(accountsRole);

        AccountsSearchResponse searchResponse = accountsRepository.findByUsernameInQuery(username).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없음"));

        return searchResponse;
    }


}