package com.my.instagram.domains.accounts.repository;

import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.accounts.dto.response.AccountsResponse;
import com.my.instagram.domains.accounts.dto.response.AccountsSearchResponse;
import com.my.instagram.domains.accounts.dto.response.ProfileSearchResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = "files")
    Optional<Accounts> findWithFilesByProfileName(String profileName);

    @Query("select count(1)" +
            " from Accounts a" +
            " where a.profileName = :profileName")
    int countByProfileName(@Param("profileName") String profileName);

    @Query("select count(1)" +
            " from Accounts a" +
            " where a.username = :username")
    int countByUsername(@Param("username") String username);

    @Query("select a " +
           " from Accounts a" +
           " left join fetch a.files fs" +
           " where a.username like :searchName" +
           " or a.profileName like :searchName" +
           " or a.name like :searchName")
    List<ProfileSearchResponse> findByName(@Param("searchName") String searchName, Pageable pageable);
}
