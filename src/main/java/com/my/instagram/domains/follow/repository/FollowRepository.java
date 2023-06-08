package com.my.instagram.domains.follow.repository;

import com.my.instagram.domains.follow.domain.Follow;
import com.my.instagram.domains.follow.dto.request.FollowBlockRequest;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    @Query("select f.*" +
            " from Follow f" +
            " where f.username = :username" +
            " and f.blockYn = 'N'")
    FollowSearchResponse findByUsernameAndBlockYn(@Param("username") String username);

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
