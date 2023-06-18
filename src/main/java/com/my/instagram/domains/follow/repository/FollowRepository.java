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
           " and f.blockYn = 'N'" +
           " and f.followAccept = 'Y'")
    Long countFollowByUsername(@Param("profileName") String profileName);

    @Query("select f" +
           " from Follow f" +
           " inner join f.accounts a" +
           " where a.profileName = :profileName" +
           " and f.blockYn = 'N'" +
           " and f.followAccept = 'Y'")
    List<FollowSearchResponse> findFollowByUsername(@Param("profileName") String profileName);

    @Query( "select count(1)" +
            " from Follow f" +
            " inner join f.followAccounts fa" +
            " where fa.profileName = :profileName" +
            " and f.blockYn = 'N'"+
            " and f.followAccept = 'Y'")
    Long countFollowerByUsername(@Param("profileName") String profileName);

    @Query( "select f" +
            " from Follow f" +
            " inner join f.followAccounts fa" +
            " where fa.profileName = :profileName" +
            " and f.blockYn = 'N'"+
            " and f.followAccept = 'Y'")
    List<FollowSearchResponse> findFollowerByUsername(@Param("profileName") String profileName);

    @Query( "select f" +
            " from Follow f" +
            " inner join f.accounts a" +
            " inner join f.followAccounts fa" +
            " where a.profileName = :profileName" +
            " and fa.profileName = :followName" +
            " and f.blockYn = 'N'")
    Follow findByProfileNameAndFollowName(@Param("profileName") String profileName,
                                          @Param("followName") String followName);

    @Query( "select f" +
            " from Follow f" +
            " inner join f.accounts a" +
            " inner join f.followAccounts fa" +
            " where a.profileName = :profileName" +
            " and fa.profileName = :followName" +
            " and f.blockYn = 'N'" +
            " and f.followAccept = :followAccept")
    Follow findAcceptByProfileNameAndFollowName(@Param("profileName") String profileName,
                                                @Param("followName") String followName,
                                                @Param("followAccept") Character followAccept);

    @Modifying
    @Query( "update Follow f" +
            " set f.blockYn = :blockYn" +
            " where f.accounts.id = :accountsId" +
            " and f.followAccounts.id = :followAccountId")
    void blockFollow(@Param("accountsId") Long accountsId,
                     @Param("followAccountId") Long followAccountId,
                     @Param("blockYn") char blockYn);

    @Modifying
    @Query( "update Follow f" +
            " set f.followAccept = :followAccept" +
            " where f.accounts.id = :accountsId" +
            " and f.followAccounts.id = :followAccountId")
    void approveFollow(@Param("accountsId") Long accountsId,
                       @Param("followAccountId") Long followAccountId,
                       @Param("followAccept") Character followAccept);

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
           " and f.blockYn = 'N'"+
           " and f.followAccept = 'Y'")
    int countByAccountsIdAndFollowName(@Param("accountsId") Long accountsId,
                                       @Param("followName") String followName);


}
