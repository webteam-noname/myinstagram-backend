package com.my.instagram.config.security;

import com.my.instagram.domains.accounts.repository.AccountsRepository;
import com.my.instagram.config.security.jwt.JwtProvider;
import com.my.instagram.config.security.jwt.exception.JwtAccessDeniedHandler;
import com.my.instagram.config.security.jwt.exception.JwtAuthenticationEntryPoint;
import com.my.instagram.config.security.jwt.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;


@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터 체인에 등록
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtProvider jwtProvider;
    private final CorsConfig corsConfig;
    private final AccountsRepository accountsRepository;
    private final String[] permitAllPaths = {"/api/auth/**",
                                             "/oauth2/authorization/google",
                                             "/api/images/**",
                                             "/favicon.ico",
                                             "/error"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);
        // 보안을 위해 csrf를 disable 처리합니다.
        http.csrf().disable()
                   .exceptionHandling()
                   .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                   .accessDeniedHandler(jwtAccessDeniedHandler);

        http.headers().frameOptions().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session을 사용하지않고 Stateless하게 로그인을 관리
            .and()
                .authorizeRequests()
                .antMatchers(permitAllPaths).permitAll()
                .antMatchers("/api/**").access("hasRole('ROLE_USER')")
            .and()
                .formLogin().disable()
                .httpBasic().disable() // 기본적인 로그인 방식을 사용하지 않습니다. // ID와 PW를 통한 인증 방식
                .apply(new CustomFilter())
            .and()
                .oauth2Login()
                .loginPage("/loginForm")
                .userInfoEndpoint()
            ;

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.httpFirewall(defaultHttpFirewall());
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }

    private class CustomFilter extends AbstractHttpConfigurer<CustomFilter, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.addFilter(corsConfig.corsFilter()) // @CrossOrigin(인증 X), 시큐리티 필터에 등록 인증(O)
                .addFilterBefore(new JwtAuthorizationFilter(accountsRepository, jwtProvider, permitAllPaths),
                                 UsernamePasswordAuthenticationFilter.class);
        }
    }
}
