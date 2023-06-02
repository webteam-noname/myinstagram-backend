package com.my.instagram.config.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth {
    private final ObjectMapper objectMapper;
    private final GoogleProperties googleProperties;

    @Override
    public String getOauthRedirectURL(){


        Map<String,Object> params=new HashMap<>();
        params.put("scope",googleProperties.getScope());
        params.put("response_type","code");
        params.put("client_id",googleProperties.getClientId());
        params.put("redirect_uri",googleProperties.getRedirectUrl());

        //parameter를 형식에 맞춰 구성해주는 함수
        String parameterString=params.entrySet()
                                     .stream()
                                     .map(x->x.getKey()+"="+x.getValue())
                                     .collect(Collectors.joining("&"));
        String redirectURL=googleProperties.getUrl()+"?"+parameterString;
        System.out.println("redirectURL = " + redirectURL);

        return redirectURL;
        /*
         * https://accounts.google.com/o/oauth2/v2/auth?scope=profile&response_type=code
         * &client_id="할당받은 id"&redirect_uri="access token 처리")
         * 로 Redirect URL을 생성하는 로직을 구성
         * */
    }
}