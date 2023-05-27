package com.my.instagram.config.mail.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties("mail.properties")
@RequiredArgsConstructor
public class MailProperties {
    private final String host;
    private final String username;
    private final String password;
    private final int port;
}

