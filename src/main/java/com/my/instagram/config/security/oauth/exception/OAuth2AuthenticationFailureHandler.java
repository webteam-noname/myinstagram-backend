package com.my.instagram.config.security.oauth.exception;

import com.my.instagram.config.security.oauth.CookieUtils;
import com.my.instagram.config.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import static com.my.instagram.config.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                                      .map(Cookie::getValue)
                                      .orElse(null);

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                                        .queryParam("error", exception.getLocalizedMessage())
                                        .build().toUriString();

        cookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request,response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
