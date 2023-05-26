package com.my.instagram.config.security.jwt.filter;

import com.auth0.jwt.interfaces.Claim;
import com.my.instagram.domains.accounts.dto.response.AccountsSearchResponse;
import com.my.instagram.domains.accounts.repository.AccountsRepository;
import com.my.instagram.config.security.auth.PrincipalDetails;
import com.my.instagram.config.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter가 있음
// /login 요청해서 username, password 요청하면(post)
// UsernamePasswordAuthenticationFilter 동작을 함

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AccountsRepository accountsRepository;
    private final JwtProvider jwtProvider;
    private final String[] permitAllPaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            System.out.println(request.getServletPath());

            if(!shouldBypassAuthentication(request)){
                String jwt = getJwtByHeader(request);
                jwtProvider.validateJwt(jwt);
                Map<String, Claim> claims = jwtProvider.decodeJwt(jwt);
                String username = claims.get("username").asString();
                setSecurityContext(username);
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        filterChain.doFilter(request,response);
    }

    // JWT필터를 적용받지 않을 URI입니다.
    public boolean shouldBypassAuthentication(HttpServletRequest request){
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String requestPath = request.getServletPath();

        for (String path : permitAllPaths) {
            if (pathMatcher.match(path, requestPath)) {
                return true;
            }
        }
        return false;
    }

    private void setSecurityContext(String username) {
        if(username != null){
            // 1. username과 password를 받아서
            AccountsSearchResponse accounts   = accountsRepository.findByUsernameInQuery(username).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없음"));
            // 2. 정상인지 로그인 시도를 합니다. authenticationManager로 로그인 시도를 하면 PrincipalDeatilsService의 loadUserByUsername가 실행됩니다.
            PrincipalDetails principalDetails = new PrincipalDetails(accounts);
            Authentication authentication     = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }else{
            throw new RuntimeException("유저를 찾을 수 없습니다.");
        }
    }

    private String getJwtByHeader(HttpServletRequest request) throws RuntimeException {
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(jwt) && jwt.startsWith(jwtProvider.getTokenType())){
            jwt = jwt.replace(jwtProvider.getTokenType(), "").trim();
            return jwt;
        }else{
            throw new RuntimeException("토큰 형식이 맞지 않습니다.");
        }
    }


    // login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    /*
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthorizationFilter: 로그인 시도중");



        // 3. PrincipleDetails를 세션에 담고
        // 4. JWT 토큰을 만들어서 응답해주면 됩니다.
        return super.attemptAuthentication(request, response);
    }*/
}
