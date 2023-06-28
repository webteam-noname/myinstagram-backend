package com.my.instagram.follow;

import com.my.instagram.domains.accounts.dto.request.ProfileUpdateRequest;
import com.my.instagram.domains.accounts.service.AccountsService;
import com.my.instagram.domains.follow.domain.Follow;
import com.my.instagram.domains.follow.dto.request.FollowApproveRequest;
import com.my.instagram.domains.follow.dto.request.FollowBlockRequest;
import com.my.instagram.domains.follow.dto.request.FollowDeleteRequest;
import com.my.instagram.domains.follow.dto.request.FollowSaveRequest;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import com.my.instagram.domains.follow.repository.FollowRepository;
import com.my.instagram.domains.follow.service.FollowService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
public class followServiceTest {


    @Autowired
    private AccountsService accountsService;

    @Autowired
    FollowService followService;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    private EntityManager em;

    @Test
    void 팔로우조회(){
        List<FollowSearchResponse> responses = followService.searchFollow("test0");
        Long searchFollowCount = followService.searchFollowCount("test0");
        Long totalCount = (long) responses.size();

        assertThrows(RuntimeException.class, () -> {
            followService.searchFollow("test12412");
        });
        for (int i = 0; i < responses.size(); i++) {
            System.out.println(responses.get(i));
        }
        System.out.println();

        assertThat(totalCount).isEqualTo(searchFollowCount);

    }

    @Test
    void 프로필명수정후_팔로우조회(){
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setChangeProfileName("수정_kimgun");
        profileUpdateRequest.setProfileIntro("수정_프로필 소개글입니다.");

        accountsService.updateProfile("test0", profileUpdateRequest,null);

        List<FollowSearchResponse> followResponses = followService.searchFollow("수정_kimgun");
        List<FollowSearchResponse> followeresponses = followService.searchFollower("수정_kimgun");
        Long searchFollowCount = followService.searchFollowCount("수정_kimgun");
        Long searchFollowerCount = followService.searchFollowerCount("수정_kimgun");
        Long followTotalCount   = (long) followResponses.size();
        Long followerTotalCount = (long) followeresponses.size();

        for (int i = 0; i < followResponses.size(); i++) {
            System.out.println(followResponses.get(i));
        }

        for (int i = 0; i < followeresponses.size(); i++) {
            System.out.println(followeresponses.get(i));
        }

        System.out.println();

        assertThat(followTotalCount).isEqualTo(searchFollowCount);
        assertThat(followerTotalCount).isEqualTo(searchFollowerCount);

    }


    @Test
    void 팔로잉조회(){
        List<FollowSearchResponse> test0 = followService.searchFollower("test0");
        int totalCount = test0.size();

        for (int i = 0; i < test0.size(); i++) {
            System.out.println(test0.get(i));
        }

        assertThat(totalCount).isEqualTo(9);
    }

    @Test
    void 맞팔로우삭제(){
        Long prevSearchFollowCount = followService.searchFollowCount("test0");
        Long prevSearchFollowerCount = followService.searchFollowerCount("test0");
        Follow existsData = followRepository.findByProfileNameAndFollowName("test0", "test2");

        FollowDeleteRequest followDeleteRequest = new FollowDeleteRequest();
        followDeleteRequest.setProfileName("test0");
        followDeleteRequest.setFollowName("test2");

        followService.deleteFollow(followDeleteRequest);

        Long searchFollowCount = followService.searchFollowCount("test0");
        Long searchFollowerCount = followService.searchFollowerCount("test0");

        Follow deleteData = followRepository.findByProfileNameAndFollowName("test0", "test2");

        assertThat(prevSearchFollowCount-1).isEqualTo(searchFollowCount);
        assertThat(prevSearchFollowerCount-1).isEqualTo(searchFollowerCount);
        assertThat(existsData.getFollowAccounts().getProfileName()).isEqualTo("test2");
        assertThat(deleteData).isNull();
    }

    @Test
    void 등록되지않은_팔로우등록(){
        FollowSaveRequest followSaveRequest = new FollowSaveRequest();
        followSaveRequest.setProfileName("test123452");
        followSaveRequest.setFollowName("test11");

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            followService.saveFollow(followSaveRequest);
        });

        assertThat("유저를 조회할 수 없습니다.").isEqualTo(runtimeException.getMessage());
    }

    @Test
    void 등록되지않은_팔로워등록(){
        FollowSaveRequest followSaveRequest = new FollowSaveRequest();
        followSaveRequest.setProfileName("test0");
        followSaveRequest.setFollowName("test1112314");

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            followService.saveFollow(followSaveRequest);
        });

        assertThat("유저를 조회할 수 없습니다.").isEqualTo(runtimeException.getMessage());
    }

    @Test
    void 팔로우등록_승인함(){
        FollowSaveRequest followSaveRequest = new FollowSaveRequest();
        followSaveRequest.setProfileName("test0");
        followSaveRequest.setFollowName("test11");

        followService.saveFollow(followSaveRequest);

        FollowApproveRequest followApproveRequest = new FollowApproveRequest();
        followApproveRequest.setProfileName("test0");
        followApproveRequest.setFollowName("test11");
        followApproveRequest.setFollowAccept('Y');

        followService.approveFollow(followApproveRequest);

        List<FollowSearchResponse> responses = followService.searchFollow("test0");

        assertThat(responses.get(responses.size() - 1).getFollowName()).isEqualTo("test11");
    }

    @Test
    void 팔로우등록_승인안함(){
        Long prevSearchFollowCount   = followService.searchFollowCount("test0");
        Long prevSearchFollowerCount = followService.searchFollowerCount("test0");

        FollowSaveRequest followSaveRequest = new FollowSaveRequest();
        followSaveRequest.setProfileName("test0");
        followSaveRequest.setFollowName("test11");
        followService.saveFollow(followSaveRequest);

        Long searchFollowCount   = followService.searchFollowCount("test0");
        Long searchFollowerCount = followService.searchFollowerCount("test0");

        assertThat(prevSearchFollowCount).isEqualTo(searchFollowCount);
        assertThat(prevSearchFollowerCount).isEqualTo(searchFollowerCount);

    }

    @Test
    void 팔로우등록_중복등록(){
        FollowSaveRequest followSaveRequest = new FollowSaveRequest();
        followSaveRequest.setProfileName("test0");
        followSaveRequest.setFollowName("test1");

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            followService.saveFollow(followSaveRequest);
        });

        assertThat("Follow는 중복될 수 없습니다.").isEqualTo(runtimeException.getMessage());
    }

    @Test
    void 팔로우차단(){

        Long prevCount = followService.searchFollowCount("test0");

        FollowBlockRequest followBlockRequest = new FollowBlockRequest();
        followBlockRequest.setProfileName("test0");
        followBlockRequest.setFollowName("test1");
        followBlockRequest.setBlockYn('Y');

        followService.blockFollow(followBlockRequest);

        Long count = followService.searchFollowCount("test0");
        assertThat(count).isEqualTo(prevCount-1);
    }
}
