package com.my.instagram.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.instagram.config.security.jwt.JwtProvider;
import com.my.instagram.domains.accounts.dto.request.AccountsCodeRequest;
import com.my.instagram.domains.accounts.dto.request.AccountsConfirmRequest;
import com.my.instagram.domains.accounts.dto.request.MailCodeRequest;
import com.my.instagram.domains.accounts.dto.request.MailUpdatePasswordRequest;
import com.my.instagram.domains.accounts.dto.response.MailCodeResponse;
import com.my.instagram.domains.accounts.repository.MailRepository;
import com.my.instagram.domains.accounts.service.AccountsService;
import com.my.instagram.domains.accounts.service.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(SpringExtension.class)
public class MailServiceTest {
    @Autowired
    private MailService mailService;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 회원가입_인증코드전송() throws Exception {
        String username = "etkim02@naver.com";
        MailCodeRequest mailCodeRequest = new MailCodeRequest();
        mailCodeRequest.setUsername(username);

        MailCodeResponse mailCodeResponse = mailService.sendJoinCodeEmail(mailCodeRequest);
        Long count = mailRepository.findCodeByUsernameAuthCodeInQuery(username, mailCodeResponse.getAuthCode());
        AccountsCodeRequest accountsCodeRequest = new AccountsCodeRequest();
        accountsCodeRequest.setUsername(username);
        accountsCodeRequest.setAuthCode(mailCodeResponse.getAuthCode());
        accountsService.inputJoinCodeEmail(accountsCodeRequest);

        assertThat(mailCodeResponse.getAuthCode()).isNotEmpty();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void 비밀번호찾기(){
        String username = "etkim02@naver.com";
        MailUpdatePasswordRequest mailUpdatePasswordRequest = new MailUpdatePasswordRequest();
        mailUpdatePasswordRequest.setUsername(username);
        String result = mailService.sendUpdatePasswordEmail(mailUpdatePasswordRequest);

        assertThat("비밀번호를 변경합니다.").isEqualTo(result);
    }

    @Test
    void 비밀번호변경_이메일전송(){
        MailUpdatePasswordRequest mailUpdatePasswordRequest = new MailUpdatePasswordRequest();
        mailUpdatePasswordRequest.setUsername("etkim02@naver.com");
        mailService.sendUpdatePasswordEmail(mailUpdatePasswordRequest);
    }


    private String generateValidJwtToken(String uidb) {
        return jwtProvider.createAccessToken(uidb, uidb,"ROLE_USER");
    }

}
