package com.my.instagram.common.mail.controller;

import com.my.instagram.common.dto.ApiResponse;
import com.my.instagram.common.mail.dto.request.MailSendRequest;
import com.my.instagram.common.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @GetMapping("/api/mail")
    public ApiResponse<Long> sendPasswordEmail(@Valid @RequestBody MailSendRequest mailSendRequest) throws MessagingException {
        return new ApiResponse<>(HttpStatus.OK, mailService.sendPasswordEmail(mailSendRequest));
    }


}
