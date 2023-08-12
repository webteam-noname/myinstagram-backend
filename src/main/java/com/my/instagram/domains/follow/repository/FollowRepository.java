package com.my.instagram.domains.follow.repository;

import com.my.instagram.domains.follow.domain.Follow;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import com.my.instagram.domains.follow.dto.response.FollowingSearchResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long> {

    // 2023-08-12 N+1 문제 해결
    // join fetch를 이용해 지연 로딩을 하지 않고 있어 N+1 문제가 발생할 수 있는 쿼리문
    // 흠.. join fetch 문제가 아닌듯 하다.
    // 제네릭을 FollowSearchResponse에서 Follow로 변경하면서 N+1 문제가 사라짐
    // 이 과정에서 FollowSearchResponse에 문제가 있음을 확인
    // 다시 inner join 해봐도 쿼리가 하나만 나옴
    // 문제를 확인한 결과 Follow에서 셀프 조인하는 followAccounts가 있는데 그걸 쿼리로 표현하지 못해 문제가 발생한것
    // followAccounts를 쿼리에 추가했다 하지만 inner join으로 사용돼서 N+1 문제가 명확히 해결되지 않았음
    // 그래서 다시 join fetch를 넣었더니 해결됨.. ^^
    @Query("select f" +
           " from Follow f" +
           " join fetch f.accounts a" +
           " join fetch f.followAccounts fa" +
           " where a.profileName = :profileName" +
           " and f.blockYn = 'N'" +
           " and f.followAccept = 'Y'")
    List<FollowSearchResponse> findFollowByUsername(@Param("profileName") String profileName);

    // 2023-08-12 N+1 문제 해결
    // findFollowByUsername의 N+1 문제를 해결해서 적용함
    @Query( "select f" +
            " from Follow f" +
            " join fetch f.accounts a" +
            " join fetch f.followAccounts fa" +
            " where fa.profileName = :profileName" +
            " and f.blockYn = 'N'"+
            " and f.followAccept = 'Y'")
    List<FollowingSearchResponse> findFollowingByUsername(@Param("profileName") String profileName);

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
    // 2023-08-08 의문사항
    // 수정과 관련된 기능을 이렇게 수정하는게 맞을까?
    @Modifying
    @Query( "update Follow f" +
            " set f.blockYn = :blockYn" +
            " where f.accounts.id = :accountsId" +
            " and f.followAccounts.id = :followAccountId")
    void blockFollow(@Param("accountsId") Long accountsId,
                     @Param("followAccountId") Long followAccountId,
                     @Param("blockYn") char blockYn);

    // 2023-08-08 의문사항
    // 수정과 관련된 기능을 이렇게 수정하는게 맞을까?
    @Modifying
    @Query( "update Follow f" +
            " set f.followAccept = :followAccept" +
            " where f.accounts.id = :accountsId" +
            " and f.followAccounts.id = :followAccountId")
    void approveFollow(@Param("accountsId") Long accountsId,
                       @Param("followAccountId") Long followAccountId,
                       @Param("followAccept") Character followAccept);

    // 2023-08-08 의문사항
    // 수정과 관련된 기능을 이렇게 수정하는게 맞을까?
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
