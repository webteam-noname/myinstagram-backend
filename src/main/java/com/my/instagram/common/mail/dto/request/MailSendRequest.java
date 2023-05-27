package com.my.instagram.common.mail.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MailSendRequest {
    private String emailReceiver;
}
