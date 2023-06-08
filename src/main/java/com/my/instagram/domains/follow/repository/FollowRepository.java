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
            " where f.username = :username" +
            " and f.blockYn = 'N'")
    Long countFollowByUsername(@Param("username") String username);

    @Query("select f" +
            " from Follow f" +
            " where f.username = :username" +
            " and f.blockYn = 'N'")
    List<FollowSearchResponse> findFollowByUsername(@Param("username") String username);

    @Query("select count(1)" +
            " from Follow f" +
            " where f.followName = :username" +
            " and f.blockYn = 'N'")
    Long countFollowerByUsername(@Param("username") String username);

    @Query("select f" +
            " from Follow f" +
            " where f.followName = :username" +
            " and f.blockYn = 'N'")
    List<FollowSearchResponse> findFollowerByUsername(@Param("username") String username);


    FollowSearchResponse findByUsernameAndFollowName(String username, String followName);

    @Modifying
    @Query("update Follow f" +
            " set f.blockYn = :blockYn" +
            " where f.username = :username" +
            " and f.followName = :followName")
    void blockFollow(@Param("username") String username,
                     @Param("followName") String followName,
                     @Param("blockYn") char blockYn);

    void deleteByUsernameAndFollowName(String username, String followName);



}
