package com.my.instagram.common.mail.service;

import com.auth0.jwt.algorithms.Algorithm;
import com.my.instagram.common.mail.domain.Mail;
import com.my.instagram.common.mail.dto.request.MailSendRequest;
import com.my.instagram.common.mail.repository.MailRepository;
import com.my.instagram.config.mail.properties.MailProperties;
import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.accounts.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final MailRepository mailRepository;
    private final AccountsRepository accountsRepository;
    private final MailProperties mailProperties;

    /* 이메일을 전송하는 프로세스입니다.
     * DB에 uuid=UUID, subStringMax=max, key=UUID.substring(0,max)를 저장합니다.
     * email주소?key=UUID.substring(0,max)를 전송합니다.
     * 비밀번호 변경 링크로 접속한 클라이언트의 key값과 uuid.substring(0,uuidMax)이 일치하는지 확인합니다.
     * 만약 일치 하지 않을 경우 에러 페이지로 처리합니다.
     **/
    public void matching(){
        String uuid = UUID.randomUUID().toString();
        String uuidNumber = uuid.replaceAll("[^0-9]","");

        int subStringMax = 0;

        for (int i = 0; i < uuidNumber.length(); i++) {
            int number  = uuidNumber.charAt(i) - '0';
            subStringMax += number;
        }

        subStringMax = subStringMax%10 == 0 ? 1 : subStringMax%10;
        String key = uuid.substring(0,subStringMax);
    }


    public String sendPasswordEmail(MailSendRequest mailSendRequest) throws MessagingException {
        Accounts accounts = accountsRepository.findByUsername(mailSendRequest.getEmailReceiver()).orElseThrow(() -> new RuntimeException("사용자 메일이 존재하지 않습니다. 다시 입력해주세요"));

        mailRepository.save(Mail.builder()
                                .accounts(accounts)
                                .build());

        createEmailForm(mailProperties.getUsername(), mailSendRequest.getEmailReceiver());

        Algorithm algorithm = Algorithm.HMAC512(String.valueOf(mailRepository.findByUsernameInQuery(accounts.getUsername())));

        return algorithm.getName();
    }


    // 메일 양식을 작성합니다.
    private MimeMessage createEmailForm(String emailSender, String emailReceiver) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(emailSender);
        message.addRecipients(Message.RecipientType.TO, emailReceiver);
        message.setSubject("insudagram 비밀번호 변경 메시지입니다.");
        message.setText("아래의 링크로 들어가서 비밀번호를 변경해주세요");
        return  message;
    }

    // 메일을 전송합니다.
    public void sentMail(String emailSender, String emailReceiver) throws MessagingException {
        //메일전송에 필요한 정보 설정
        MimeMessage emailForm = createEmailForm(emailSender, emailReceiver);

        //실제 메일 전송
        javaMailSender.send(emailForm);
    }
}
