package com.my.instagram.common.mail.repository;

import com.my.instagram.common.mail.domain.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MailRepository extends JpaRepository<Mail,Long> {

    @Query("select m.id from Mail m" +
            " inner join fetch a.accounts a" +
            " where a.username = :username")
    Long findByUsernameInQuery(@Param("username") String username);
}
