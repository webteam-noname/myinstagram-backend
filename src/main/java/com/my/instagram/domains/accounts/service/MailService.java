package com.my.instagram.domains.accounts.service;

import com.my.instagram.config.mail.properties.MailProperties;
import com.my.instagram.config.security.jwt.JwtProvider;
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
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService extends EmailLogin{

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;
    private final MailRepository mailRepository;
    private final AccountsRepository accountsRepository;
    private final JwtProvider jwtProvider;

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
    public MailCodeResponse sendJoinCodeEmail(MailCodeRequest mailSendRequest) {
        // 회원 중복 여부 체크
        usernameOverTwiceExistsException(mailSendRequest.getUsername());
        profileNameOverTwiceExistsException(mailSendRequest.getProfileName());

        String ePw = createKey(); // 랜덤 인증번호 생성
        String to  = mailSendRequest.getUsername(); // 이메일 받는 사람

        InternetAddress from = null;

        try {
            from = new InternetAddress(mailProperties.getUsername(), "insudagram");

            MimeMessage message = createEmailForm(from, to, ePw); // 메일 발송

            javaMailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // 인증 코드를 서버에 저장합니다.
        mailRepository.save(Mail.builder()
                                .username(to)
                                .authCode(ePw)
                                .build());

        return new MailCodeResponse(ePw); // 메일로 보냈던 인증 코드를 서버로 반환
    }

    public void usernameOverTwiceExistsException(String username) {
        if(accountsRepository.countByUsername(username) > 0){
            throw new RuntimeException("사용자 ID는 중복될 수 없습니다.");
        }
    }

    public void profileNameOverTwiceExistsException(String profileName) {
        System.out.println(accountsRepository.countByProfileName(profileName));
        if(accountsRepository.countByProfileName(profileName) > 0){
            throw new RuntimeException("프로필 명은 중복될 수 없습니다.");
        }
    }

    private AccountsSearchResponse validateAccount(String username) {
        return accountsRepository.findByUsernameInQuery(username)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없음."));
    }

    // 비밀번호 인증코드 유효성을 검증합니다
    public boolean validateJoinCode(String username, String authCode) {
        Long isExist = mailRepository.findCodeByUsernameAuthCodeInQuery(username,authCode);

        return isExist == 0L;
    }

    // 메일 양식을 작성합니다.
    private MimeMessage createEmailForm(InternetAddress from, String to, String ePw) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(from);
        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("insudagram 회원 인증 메시지입니다.");
        message.setText("회원 인증코드는 " + ePw + "입니다.");
        return  message;
    }

    // 비밀번호 변경 인증코드를 삭제합니다.
    public void deletePasswordCode(String username) {
        mailRepository.deleteByUsername(username);
    }

    private String generateValidJwtToken(String uidb) {
        return jwtProvider.createAccessToken(uidb, uidb,"ROLE_USER");
    }

    private MimeMessage createUpdateEmailForm(InternetAddress from, String to) throws MessagingException {
        UUID tempEmailUsername = UUID.randomUUID();
        String uidb = tempEmailUsername.toString().substring(0,4);
        String accessToken = generateValidJwtToken(uidb);
        putEmailLogin(uidb, accessToken, to);
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(from);
        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("insudagram 비밀번호 변경 관련 메시지입니다.");
        message.setContent("<div>\n" +
                                " <table align = \"center\" \n" +
                                "     border = \"0\"\n" +
                                "     cellspacing = \"0\"\n" +
                                "   cellpadding = \"0\"\n" +
                                "   style = \"border-collapse:collapse;\"> \n" +
                                "  <tbody> \n" +
                                "   <tr> \n" +
                                "    <td style = \"font-family:Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;background:#ffffff;\" > \n" +
                                "     <table border = \"0\" width = \"100%\" cellspacing = \"0\" cellpadding = \"0\" style = \"border-collapse:collapse;\" > \n" +
                                "      <tbody > \n" +
                                "       <tr style = \"\" > \n" +
                                "        <td height = \"20\" style = \"line-height:20px;\" colspan = \"3\" >  </td>\n" +
                                "       </tr > \n" +
                                "       <tr > \n" +
                                "        <td height = \"1\" colspan = \"3\" style = \"line-height:1px;\" > </td>\n" +
                                "       </tr > \n" +
                                "       <tr > \n" +
                                "        <td style = \"\" > \n" +
                                "         <table border = \"0\" width = \"100%\" cellspacing = \"0\" cellpadding = \"0\"\n" +
                                "             style = \"border-collapse:collapse;text-align:center;html_width:100%;width:100%;\" > \n" +
                                "            <tbody > \n" +
                                "          <tr > \n" +
                                "           <td width = \"15px\" style = \"width:15px;\" > \n" +
                                "           </td>\n" +
                                "           <td width=\"15 px \" style=\"width: 15 px;\"></td>\n" +
                                "          </tr>\n" +
                                "            </tbody>\n" +
                                "            </table>\n" +
                                "        </td>\n" +
                                "       </tr>\n" +
                                "       <tr>\n" +
                                "       <td style=\"\">\n" +
                                "        <table border=\"0 \" width=\"430 \" cellspacing=\"0 \" cellpadding=\"0 \" style=\"border - collapse: collapse;margin: 0 auto 0 auto;\">\n" +
                                "         <tbody>\n" +
                                "          <tr>\n" +
                                "           <td style=\"\">\n" +
                                "            <table border=\"0 \" width=\"430 px \" cellspacing=\"0 \" cellpadding=\"0 \" style=\"border - collapse: collapse; margin: 0 auto 0 auto;width: 430 px;\">\n" +
                                "             <tbody>\n" +
                                "              <tr>\n" +
                                "               <td width=\"20 \" style=\"display: block;width: 20 px;\">\n" +
                                "                &nbsp;&nbsp;&nbsp;\n" +
                                "               </td>\n" +
                                "               <td style=\"\">\n" +
                                "                <p style=\"margin: 10 px 0 10 px 0; color: #565a5c;font-size:18px;\">\n" +
                                "                 "+to+"님, 안녕하세요\n" +
                                "                </p>\n" +
                                "                <p style= \"margin:10px 0 10px 0;color:#565a5c;font-size:18px;\" >\n" +
                                "                 Insudagram 로그인과 관련하여 불편을 끼쳐드려 죄송합니다.\n" +
                                "                 비밀번호를 잊으셨나요? 회원님이 로그인한 것이 맞다면 지금 바로 계정에 로그인하거나비밀번호를 재설정할 수 있습니다. \n" +
                                "                </p>\n" +
                                "               </td > \n" +
                                "              </tr>\n" +
                                "              <tr style=\"\">\n" +
                                "               <td height=\"20\" style=\"line-height:20px;\">\n" +
                                "                &nbsp;\n" +
                                "               </td > \n" +
                                "              </tr>\n" +
                                "              <tr>\n" +
                                "               <td width=\"20\" style=\"display:block;width:20px;\">&nbsp;&nbsp;&nbsp;</td > \n" +
                                "               <td style = \"\" > \n" +
                                "                <a href = \"http://10.90.1.111:8080/api/auth/accounts/passwords/reset/sign-in/confirmations?uidb="+uidb+"&amp;accessToken="+accessToken+"\"\n" +
                                "                 style = \"color:#1b74e4;text-decoration:none;display:block;:370px;\"\n" +
                                "                 rel = \"noreferrer noopener\"\n" +
                                "                 target = \"_blank\" > \n" +
                                "                 <table border = \"0\" width = \"390\" cellspacing = \"0\" cellpadding = \"0\" style = \"border-collapse:collapse;\" > \n" +
                                "                  <tbody > \n" +
                                "                   <tr > \n" +
                                "                    <td style = \"border-collapse:collapse;border-radius:3px;text-align:center;display:block;border:solid 1px #009fdf;padding:10px 16px 14px 16px;margin:0 2px 0 auto;min-width:80px;background-color:#47A2EA;\" > \n" +
                                "                     <a href = \"http://10.90.1.111:8080/api/auth/accounts/passwords/reset/sign-in/confirmations?uidb="+uidb+"&amp;accessToken="+accessToken+"\"\n" +
                                "                      style = \"color:#1b74e4;text-decoration:none;display:block;\"\n" +
                                "                      rel = \"noreferrer noopener\"\n" +
                                "                      target = \"_blank\" > \n" +
                                "                      <center > \n" +
                                "                       <font size = \"3\" > \n" +
                                "                        <span style = \"font-family:Helvetica Neue,Helvetica,Roboto,Arial,sans-serif;white-space:nowrap;font-weight:bold;vertical-align:middle;color:#fdfdfd;font-size:16px;line-height:16px;\" > \n" +
                                "                         "+to+"(으)로 로그인\n" +
                                "                        </span>\n" +
                                "                       </font > \n" +
                                "                      </center>\n" +
                                "                      </a > \n" +
                                "                     </td>\n" +
                                "                    </tr > \n" +
                                "                   </tbody>\n" +
                                "                  </table > \n" +
                                "                 </a>\n" +
                                "                </td > \n" +
                                "               </tr>\n" +
                                "               <tr>\n" +
                                "               <td width=\"20\" style=\"display:block;width:20px;\">&nbsp;&nbsp;&nbsp;</td > \n" +
                                "               <td style = \"\" > \n" +
                                "                <table border = \"0\" width = \"100%\"cellspacing = \"0\"cellpadding = \"0\"style = \"border-collapse:collapse;\" > \n" +
                                "                 <tbody > \n" +
                                "                  <tr > \n" +
                                "                   <td style = \"\" > \n" +
                                "                    <table border = \"0\" cellspacing = \"0\" cellpadding = \"0\" style = \"border-collapse:collapse;\" > \n" +
                                "                     <tbody > \n" +
                                "                      <tr > \n" +
                                "                       <td style = \"\" > \n" +
                                "                        <table border = \"0\" cellspacing = \"0\" cellpadding = \"0\" style = \"border-collapse:collapse;\" > \n" +
                                "                         <tbody > \n" +
                                "                          <tr > </tr>\n" +
                                "                          <tr style=\"\">\n" +
                                "                           <td height=\"20\" style=\"line-height:20px;\">&nbsp;</td > \n" +
                                "                          </tr>\n" +
                                "                          <tr>\n" +
                                "                           <td style=\"\">\n" +
                                "                            <a href=\"http://10.90.1.111:8080/api/auth/accounts/passwords/reset/confirmations?uidb="+uidb+"&amp;accessToken="+accessToken+"\" \n" +
                                "                             style=\"color: #1b74e4;text-decoration:none;display:block;:370px;\" \n" +
                                "                             rel= \"noreferrer noopener\"\n" +
                                "                             target = \"_blank\" > \n" +
                                "                             <table border = \"0\" width = \"390\" cellspacing = \"0\" cellpadding = \"0\"style = \"border-collapse:collapse;\" > \n" +
                                "                              <tbody > \n" +
                                "                               <tr > \n" +
                                "                                <td style = \"border-collapse:collapse;border-radius:3px;text-align:center;display:block;border:solid 1px #009fdf;padding:10px 16px 14px 16px;margin:0 2px 0 auto;min-width:80px;background-color:#47A2EA;\" > \n" +
                                "                                 <a href = \"http://10.90.1.111:8080/api/auth/accounts/passwords/reset/confirmations?uidb="+uidb+"&amp;accessToken="+accessToken+"\"\n" +
                                "                                  style = \"color:#1b74e4;text-decoration:none;display:block;\"\n" +
                                "                                  rel = \"noreferrer noopener\"\n" +
                                "                                  target = \"_blank\" > \n" +
                                "                                  <center > \n" +
                                "                                   <font size = \"3\" > \n" +
                                "                                    <span style = \"font-family:Helvetica Neue,Helvetica,Roboto,Arial,sans-serif;white-space:nowrap;font-weight:bold;vertical-align:middle;color:#fdfdfd;font-size:16px;line-height:16px;\" > \n" +
                                "                                     비밀번호 재설정 \n" +
                                "                                    </span>\n" +
                                "                                   </font > \n" +
                                "                                  </center>\n" +
                                "                                 </a > \n" +
                                "                                </td>\n" +
                                "                               </tr > \n" +
                                "                              </tbody>\n" +
                                "                             </table > \n" +
                                "                            </a>\n" +
                                "                           </td > \n" +
                                "                          </tr>\n" +
                                "                          <tr style=\"\">\n" +
                                "                           <td height=\"20\" style=\"line-height:20px;\">&nbsp;</td > \n" +
                                "                          </tr>\n" +
                                "                          <tr>\n" +
                                "                           <td width=\"15\" style=\"display:block;width:15px;\">&nbsp;&nbsp;&nbsp;</td > \n" +
                                "                          </tr>\n" +
                                "                          <tr></tr > \n" +
                                "                         </tbody>\n" +
                                "                        </table > \n" +
                                "                       </td>\n" +
                                "                      </tr > \n" +
                                "                      <tr > \n" +
                                "                       <td width = \"20\" style = \"display:block;width:20px;\" > \n" +
                                "                           \n" +
                                "                       </td>\n" +
                                "                      </tr > \n" +
                                "                     </tbody>\n" +
                                "                    </table > \n" +
                                "                   </td>\n" +
                                "                  </tr > \n" +
                                "                 </tbody>\n" +
                                "                </table > \n" +
                                "               </td>\n" +
                                "              </tr > \n" +
                                "             </tbody>\n" +
                                "            </table > \n" +
                                "           </td>\n" +
                                "          </tr > \n" +
                                "         </tbody>\n" +
                                "        </table > \n" +
                                "       </td>\n" +
                                "      </tr > \n" +
                                "     </tbody>\n" +
                                "    </table>\n" +
                                "   </td>\n" +
                                "  </tr>\n" +
                                " </tbody>\n" +
                                "</table>\n" +
                                "</div>", "text/html; charset=utf-8");
        return  message;
    }

    public String sendUpdatePasswordEmail(MailUpdatePasswordRequest mailUpdatePasswordRequest) {
        String to  = mailUpdatePasswordRequest.getUsername(); // 이메일 받는 사람
        accountsRepository.findByUsername(to)
                          .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없음"));

        try {
            InternetAddress from = new InternetAddress(mailProperties.getUsername(), "insudagram");
            MimeMessage message = createUpdateEmailForm(from, to); // 메일 발송
            javaMailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return "비밀번호를 변경합니다.";
    }



}
