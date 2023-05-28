package com.my.instagram.domains.accounts.dto.response;

import com.my.instagram.domains.accounts.domain.Mail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MailCodeResponse {
    private String authCode;

    public MailCodeResponse(String authCode) {
        this.authCode = authCode;
    }

}
