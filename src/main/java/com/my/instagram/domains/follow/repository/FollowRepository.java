package com.my.instagram.domains.follow.repository;

import com.my.instagram.domains.follow.domain.Follow;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    @Query("select count(1)" +
           " from Follow f" +
           " inner join f.accounts a" +
           " where a.profileName = :profileName" +
           " and f.blockYn = 'N'")
    Long countFollowByUsername(@Param("profileName") String profileName);

    @Query("select f" +
           " from Follow f" +
           " inner join f.accounts a" +
           " where a.profileName = :profileName" +
           " and f.blockYn = 'N'")
    List<FollowSearchResponse> findFollowByUsername(@Param("profileName") String profileName);

    @Query( "select count(1)" +
            " from Follow f" +
            " inner join f.followAccounts fa" +
            " where fa.profileName = :profileName" +
            " and f.blockYn = 'N'")
    Long countFollowerByUsername(@Param("profileName") String profileName);

    @Query( "select f" +
            " from Follow f" +
            " inner join f.followAccounts fa" +
            " where fa.profileName = :profileName" +
            " and f.blockYn = 'N'")
    List<FollowSearchResponse> findFollowerByUsername(@Param("profileName") String profileName);

    @Query( "select f" +
            " from Follow f" +
            " inner join f.accounts a" +
            " inner join f.followAccounts fa" +
            " where a.profileName = :profileName" +
            " and fa.profileName = :followName" +
            " and f.blockYn = 'N'")
    FollowSearchResponse findByProfileNameAndFollowName(@Param("profileName") String profileName,
                                                        @Param("followName") String followName);

    @Modifying
    @Query( "update Follow f" +
            " set f.blockYn = :blockYn" +
            " where f.accounts.id = :accountsId" +
            " and f.followAccounts.id = :followAccountId")
    void blockFollow(@Param("accountsId") Long accountsId,
                     @Param("followAccountId") Long followAccountId,
                     @Param("blockYn") char blockYn);

    @Modifying
    @Query("delete from Follow f"+
            " where f.accounts.id = :accountsId" +
            " and f.followAccounts.id = :followAccountsId")
    int deleteByAccountsIdAndFollowAccountsId(@Param("accountsId") Long accountsId,
                                              @Param("followAccountsId") Long followAccountsId);

    @Query("select count(1)" +
           " from Follow as f" +
           " inner join f.accounts a" +
           " inner join f.followAccounts fa" +
           " where a.id = :accountsId" +
           " and fa.profileName = :followName" +
           " and f.blockYn = 'N'")
    int countByAccountsIdAndFollowName(@Param("accountsId") Long accountsId,
                                       @Param("followName") String followName);
}
