package com.my.instagram.domains.follow.repository;

import com.my.instagram.domains.follow.domain.Follow;
import com.my.instagram.domains.follow.dto.request.FollowBlockRequest;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    FollowSearchResponse findByUsername(String username);

    Follow findByUsernameFollow(String username, String follow);

    @Query("update from Follow f" +
            " set f.blockYn = :blockYn" +
            " where f.username = :username" +
            " and f.follow = :follow")
    void blockFollow(@Param("username") String username,
                     @Param("follow") String follow,
                     @Param("blockYn") Character blockYn);
}
