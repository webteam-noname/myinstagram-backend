package com.my.instagram.domains.accounts.repository;

import com.my.instagram.domains.accounts.domain.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MailRepository extends JpaRepository<Mail,Long> {
    @Query("select COUNT(m) from Mail m" +
            " where m.username = :username" +
            "   and m.authCode = :authCode ")
    public Long findCodeByUsernameAuthCodeInQuery(@Param("username") String username,
                                                  @Param("authCode") String authCode);

    public int deleteByUsername(String username);
}
