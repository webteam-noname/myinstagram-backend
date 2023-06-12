package com.my.instagram.domains.follow.repository;

import com.my.instagram.domains.follow.domain.Follow;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    @Query(value = "select count(1)" +
            " from Follow f" +
            " inner join fetch f.accounts a" +
            " where a.profileName = :profileName" +
            " and f.blockYn = 'N'",nativeQuery = true)
    Long countFollowByUsername(@Param("profileName") String profileName);

    @Query(value = "select f" +
            " from Follow f" +
            " inner join fetch f.accounts a" +
            " where a.profileName = :profileName" +
            " and f.blockYn = 'N'",nativeQuery = true)
    List<FollowSearchResponse> findFollowByUsername(@Param("profileName") String profileName);

    @Query(value = "select count(1)" +
            " from Follow f" +
            " inner join fetch f.accounts a" +
            " where a.followName = :profileName" +
            " and f.blockYn = 'N'",nativeQuery = true)
    Long countFollowerByUsername(@Param("profileName") String profileName);

    @Query(value = "select f" +
            " from Follow f" +
            " inner join fetch f.accounts a" +
            " where a.followName = :profileName" +
            " and f.blockYn = 'N'",nativeQuery = true)
    List<FollowSearchResponse> findFollowerByUsername(@Param("profileName") String profileName);

    @Query(value = "select count(1)" +
                    " from Follow f" +
                    " inner join fetch f.accounts a" +
                    " where a.profileName = :profileName" +
                    " and f.followName = :followName" +
                    " and f.blockYn = 'N'",nativeQuery = true)
    FollowSearchResponse findByProfileNameAndFollowName(@Param("profileName") String profileName,
                                                        @Param("followName") String followName);

    @Modifying
    @Query(value = "update Follow f" +
            " set f.blockYn = :blockYn" +
            " where f.accounts.profileName = :profileName" +
            " and f.followName = :followName",nativeQuery = true)
    void blockFollow(@Param("profileName") String profileName,
                     @Param("followName") String followName,
                     @Param("blockYn") char blockYn);

    @Modifying
    @Query(value = "delete from Follow f" +
                    " where f.accounts.profileName = :profileName" +
                    " and f.followName = :followName",nativeQuery = true)
    void deleteByProfileNameAndFollowName(@Param("profileName") String profileName,
                                          @Param("followName") String followName);

    @Query(value = "select count(1)" +
            " from Follow f" +
            " inner join fetch f.accounts a" +
            " where a.profileName = :profileName" +
            " and f.followName = :followName"+
            " and f.blockYn = 'N'",nativeQuery = true)
    int countByProfileNameAndFollowName(@Param("profileName") String profileName, @Param("followName") String followName);
}
