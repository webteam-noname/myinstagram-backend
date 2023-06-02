package com.my.instagram.config.security.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;


@Getter
@ConstructorBinding
@ConfigurationProperties("spring.security.oauth2.client.registration.google")
@RequiredArgsConstructor
public class GoogleProperties {
    private final String url;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUrl;
    private final String scope;
}
