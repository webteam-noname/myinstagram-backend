package com.my.instagram.domains.accounts.repository;

import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.accounts.dto.response.AccountsSearchResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts,Long> {

    @Query("select a from Accounts a" +
            " inner join fetch a.accountsRoles ar" +
            " inner join fetch ar.role r" +
            " where a.username = :username")
    public Optional<AccountsSearchResponse> findByUsernameInQuery(@Param("username") String username);

    // public List<AccountsSearchResponse> findAllAccounts();

    Optional<Accounts> findByUsername(String username);

}
