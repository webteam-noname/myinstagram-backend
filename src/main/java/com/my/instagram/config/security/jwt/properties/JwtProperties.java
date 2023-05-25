package com.my.instagram.config.security.jwt.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties("jwt.properties")
@RequiredArgsConstructor
public class JwtProperties {
    private final String secret;
    private final Long expiredAt;
    private final Long refreshExpiredAt;
    private final String type;
}