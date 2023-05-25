package com.my.instagram.config.security.auth;

import com.my.instagram.domains.accounts.dto.response.AccountsSearchResponse;
import com.my.instagram.domains.accounts.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 설정에서 loginProcessingUrl("/login")
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IOC되어 있는 loadUserByUsername 함수가 실행
@Service
@RequiredArgsConstructor
public class PrincipalDeatilsService implements UserDetailsService {

    private final AccountsRepository accountsRepository;

    // form의 name과 파라미터의 name이 같아야함
    // 시큐리티 session = Authentication = 내부 UserDetails
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountsSearchResponse accountResponse = accountsRepository.findByUsernameInQuery(username).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없음"));

        if(accountResponse != null){
            return new PrincipalDetails(accountResponse);
        }else{
            throw new UsernameNotFoundException("조회된 데이터가 없습니다.");
        }
    }
}
