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
            " inner join fetch f.accounts a" +
            " where a.profileName = :profileName" +
            " and f.blockYn = 'N'")
    Long countFollowByUsername(@Param("profileName") String profileName);

    @Query("select f" +
            " from Follow f" +
            " inner join fetch f.accounts a" +
            " where a.profileName = :profileName" +
            " and f.blockYn = 'N'")
    List<FollowSearchResponse> findFollowByUsername(@Param("profileName") String profileName);

    @Query("select count(1)" +
            " from Follow f" +
            " inner join fetch f.accounts a" +
            " where a.followName = :profileName" +
            " and f.blockYn = 'N'")
    Long countFollowerByUsername(@Param("profileName") String profileName);

    @Query("select f" +
            " from Follow f" +
            " inner join fetch f.accounts a" +
            " where a.followName = :profileName" +
            " and f.blockYn = 'N'")
    List<FollowSearchResponse> findFollowerByUsername(@Param("profileName") String profileName);


    FollowSearchResponse findByUsernameAndFollowName(String username, String followName);

    @Modifying
    @Query("update Follow f" +
            " set f.blockYn = :blockYn" +
            " where f.accounts.profileName = :profileName" +
            " and f.followName = :followName")
    void blockFollow(@Param("profileName") String profileName,
                     @Param("followName") String followName,
                     @Param("blockYn") char blockYn);

    void deleteByProfileNameAndFollowName(String profileName, String followName);

    @Query("select count(1)" +
            " from Follow f" +
            " inner join fetch f.accounts a" +
            " where a.profileName = :profileName" +
            "   and f.followName = :followName"+
            "   and f.blockYn = 'N'")
    int countByProfileNameAndFollowName(@Param("profileName") String profileName, @Param("followName") String followName);
}
