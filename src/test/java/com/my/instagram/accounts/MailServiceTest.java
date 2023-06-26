package com.my.instagram.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.instagram.config.security.jwt.JwtProvider;
import com.my.instagram.domains.accounts.dto.request.AccountsCodeRequest;
import com.my.instagram.domains.accounts.dto.request.AccountsConfirmRequest;
import com.my.instagram.domains.accounts.dto.request.MailCodeRequest;
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
        String result = accountsService.inputJoinCodeEmail(accountsCodeRequest);

        assertThat(mailCodeResponse.getAuthCode()).isNotEmpty();
        assertThat(count).isEqualTo(1);
        assertThat("인증 코드 입력이 완료됐습니다.").isEqualTo(result);
    }

    @Test
    void 비밀번호찾기(){
        String username = "etkim02@naver.com";
        MailCodeRequest mailSendRequest = new MailCodeRequest();
        mailSendRequest.setUsername(username);
        String result = mailService.sendUpdatePasswordEmail(mailSendRequest);

        assertThat("비밀번호를 변경합니다.").isEqualTo(result);
    }

    @Test
    void 비밀번호변경_이메일전송(){
        MailCodeRequest mailSendRequest = new MailCodeRequest();
        mailSendRequest.setUsername("etkim02@naver.com");
        mailService.sendUpdatePasswordEmail(mailSendRequest);
    }

    @Test
    void UUID테스트(){
        UUID tempEmailUsername = UUID.randomUUID();
        String uidb = tempEmailUsername.toString().substring(0,4);
        String accessToken = generateValidJwtToken(uidb);
        System.out.println(uidb);
        System.out.println(accessToken);
    }

    private String generateValidJwtToken(String uidb) {
        return jwtProvider.createAccessToken(uidb, uidb,"ROLE_USER");
    }

    @Test
    void vue페이지열기() throws Exception {
        UUID tempEmailUsername = UUID.randomUUID();
        String uidb = tempEmailUsername.toString().substring(0,4);
        String accessToken = generateValidJwtToken(uidb);
        mailService.putEmailLogin(uidb, accessToken, "etkim02@naver.com");

        AccountsConfirmRequest accountsConfirmRequest = new AccountsConfirmRequest();
        accountsConfirmRequest.setUidb(uidb);
        accountsConfirmRequest.setAccessToken(accessToken);

        String saveDtoJsonString = objectMapper.writeValueAsString(accountsConfirmRequest);
        // System.out.println(saveDtoJsonString);

        MvcResult result = mockMvc.perform(get("/api/auth/accounts/password/reset/confirm")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(saveDtoJsonString))
                                    .andDo(print())
                                    .andReturn();
    }
}
