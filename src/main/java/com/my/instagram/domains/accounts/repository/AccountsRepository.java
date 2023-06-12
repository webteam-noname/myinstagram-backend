package com.my.instagram.domains.accounts.repository;

import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.accounts.dto.response.AccountsResponse;
import com.my.instagram.domains.accounts.dto.response.AccountsSearchResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts,Long>, AccountsRepositoryCustom {

    @Query("select a from Accounts a" +
            " inner join fetch a.accountsRoles ar" +
            " inner join fetch ar.role r" +
            " where a.username = :username")
    public Optional<AccountsSearchResponse> findByUsernameInQuery(@Param("username") String username);

    Optional<Accounts> findByUsername(String username);

    Optional<Accounts> findByProfileName(String profileName);

    @Query("select count(1)" +
            " from Accounts a" +
            " where a.profileName = :profileName")
    int countByProfileName(@Param("profileName") String profileName);

    @Query("select count(1)" +
            " from Accounts a" +
            " where a.username = :username")
    int countByUsername(@Param("username") String username);

    @Query("select a from Accounts a" +
            " where a.username like :name" +
            " or a.profileName like :name" +
            " or a.name like :name")
    AccountsResponse findByName(@Param("name") String name);
}
