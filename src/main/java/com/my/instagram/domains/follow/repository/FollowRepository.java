package com.my.instagram.domains.follow.repository;

import com.my.instagram.domains.follow.domain.Follow;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
            " inner join f.accounts a" +
            " where f.followName = :profileName" +
            " and f.blockYn = 'N'")
    Long countFollowerByUsername(@Param("profileName") String profileName);

    @Query( "select f" +
            " from Follow f" +
            " inner join f.accounts a" +
            " where f.followName = :profileName" +
            " and f.blockYn = 'N'")
    List<FollowSearchResponse> findFollowerByUsername(@Param("profileName") String profileName);

    @Query( "select f" +
            " from Follow f" +
            " inner join f.accounts a" +
            " where a.profileName = :profileName" +
            " and f.followName = :followName" +
            " and f.blockYn = 'N'")
    FollowSearchResponse findByProfileNameAndFollowName(@Param("profileName") String profileName,
                                                        @Param("followName") String followName);

    @Modifying
    @Query( "update Follow f" +
            " set f.blockYn = :blockYn" +
            " where f.accounts.id = :accountsId" +
            " and f.followName = :followName")
    void blockFollow(@Param("accountsId") Long accountsId,
                     @Param("followName") String followName,
                     @Param("blockYn") char blockYn);

    @Modifying
    @Query("delete from Follow f"+
            " where f.accounts.id = :accountsId" +
            " and f.followName = :followName")
    int deleteByAccountsIdAndFollowName(@Param("accountsId") Long accountsId,
                                        @Param("followName") String followName);

    @Query("select count(1)" +
           " from Follow as f" +
           " inner join f.accounts a" +
           " where a.id = :accountsId" +
           " and f.followName = :followName" +
           " and f.blockYn = 'N'")
    int countByAccountsIdAndFollowName(@Param("accountsId") Long accountsId,
                                       @Param("followName") String followName);
}
