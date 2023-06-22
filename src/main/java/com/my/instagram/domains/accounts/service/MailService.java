package com.my.instagram.domains.accounts.service;

import com.my.instagram.config.mail.properties.MailProperties;
import com.my.instagram.domains.accounts.domain.Mail;
import com.my.instagram.domains.accounts.dto.request.MailCodeRequest;
import com.my.instagram.domains.accounts.dto.request.MailUpdatePasswordRequest;
import com.my.instagram.domains.accounts.dto.response.AccountsSearchResponse;
import com.my.instagram.domains.accounts.dto.response.MailCodeResponse;
import com.my.instagram.domains.accounts.repository.AccountsRepository;
import com.my.instagram.domains.accounts.repository.MailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;
    private final MailRepository mailRepository;
    private final AccountsRepository accountsRepository;

    // 랜덤 인증 코드 전송
    public String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤, rnd 값에 따라서 아래 switch 문이 실행됨

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    // a~z (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    // A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }

        return key.toString();
    }

    // 메일 발송
    public MailCodeResponse sendJoinCodeEmail(MailCodeRequest mailSendRequest) throws Exception {
        String ePw = createKey(); // 랜덤 인증번호 생성
        String to  = mailSendRequest.getUsername(); // 이메일 받는 사람

        AccountsSearchResponse accountsSearchResponse = validateAccount(to);// 유저의 정보를 조회

        InternetAddress from = new InternetAddress(mailProperties.getUsername(), "insudagram");

        // TODO Auto-generated method stub
        MimeMessage message = createEmailForm(from, to, ePw); // 메일 발송
        try {// 예외처리
            javaMailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }

        // 인증 코드를 서버에 저장합니다.
        mailRepository.save(Mail.builder()
                                .username(to)
                                .authCode(ePw)
                                .build());

        return new MailCodeResponse(ePw); // 메일로 보냈던 인증 코드를 서버로 반환
    }

    // 비밀번호 인증코드 유효성을 검증합니다
    public boolean validatePasswordCode(String username, String authCode) {
        AccountsSearchResponse accountsSearchResponse = validateAccount(username);// 유저의 정보를 조회
        Long isExist = mailRepository.findCodeByUsernameAuthCodeInQuery(username,authCode);

        return isExist == 0L;
    }

    private AccountsSearchResponse validateAccount(String username) {
        return accountsRepository.findByUsernameInQuery(username)
                                 .orElseThrow(() -> new RuntimeException("해당 유저가 없음."));
    }

    // 메일 양식을 작성합니다.
    private MimeMessage createEmailForm(InternetAddress from, String emailReceiver, String ePw) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(from);
        message.addRecipients(Message.RecipientType.TO, emailReceiver);
        message.setSubject("insudagram 비밀번호 변경 메시지입니다.");
        message.setText("비밀번호 변경 인증코드는 " + ePw + "입니다.");
        return  message;
    }

    // 비밀번호 변경 인증코드를 삭제합니다.
    public void deletePasswordCode(String username) {
        mailRepository.deleteByUsername(username);
    }

    public String sendUpdatePasswordEmail(MailUpdatePasswordRequest mailUpdatePasswordRequest) {
        return "";
    }
}
