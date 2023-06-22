package com.my.instagram.config.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.my.instagram.config.security.auth.PrincipalDetails;
import com.my.instagram.config.security.jwt.dto.JwtDto;
import com.my.instagram.config.security.jwt.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                                       .map(GrantedAuthority::getAuthority)
                                       .collect(Collectors.joining(","));

        return createAccessToken(authentication.getName(),"",authorities);
    }

    public JwtDto createJwtDto(PrincipalDetails principalDetails) {
        List<String> roleList = new ArrayList<>();
        principalDetails.getAuthorities().forEach(ad -> roleList.add(ad.getAuthority()));

        String accessToken = createAccessToken(
                principalDetails.getAccountResponse().getUsername(),
                principalDetails.getAccountResponse().getName(),
                String.join(", ", roleList)
        );

        String refreshToken = createRefreshToken();
        return new JwtDto(jwtProperties.getType(), accessToken, refreshToken);
    }

    private String createRefreshToken() {
        return JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpiredAt()))
                .sign(Algorithm.HMAC512(jwtProperties.getSecret()));
    }

    public String createAccessToken(String username, String realName, String roleType) {
        return JWT.create()
                  .withSubject(username)
                  .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiredAt()))
                  .withClaim("username",username)
                  .withClaim("realName", realName)
                  .withClaim("roleType", roleType)
                  .sign(Algorithm.HMAC512(jwtProperties.getSecret()));
    }

    public void validateJwt(String jwt) {
        JWT.require(Algorithm.HMAC512(jwtProperties.getSecret())).build().verify(jwt);
    }

    public Map<String, Claim> decodeJwt(String jwt) {
        return JWT.decode(jwt).getClaims();
    }

    public String getTokenType() {
        return jwtProperties.getType();
    }
}
