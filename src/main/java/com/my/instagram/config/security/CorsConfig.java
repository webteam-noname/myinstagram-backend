package com.my.instagram.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import java.util.List;

@Component
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config               = new CorsConfiguration();

        config.setAllowCredentials(true); // 내 서버가 응답을 할 때 json을 자바스크립트에서 처리할 수 있게 할지를 설정하는 것
        config.addAllowedOriginPattern("*"); // 모든 ip에 응답을 허용하겠습니다.
        config.setAllowedOriginPatterns(List.of("http://localhost", "http://192.168.101.182:8080"));
        config.addAllowedHeader("*"); // 모든 header에 응답을 허용합니다.
        config.setAllowedMethods(List.of("GET", "POST", "DELETE","OPTIONS","HEAD","PUT", "PATCH"));
        config.setExposedHeaders(List.of("*"));
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
