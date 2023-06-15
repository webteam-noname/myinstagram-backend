package com.my.instagram.follow;

import com.my.instagram.domains.follow.dto.request.FollowDeleteRequest;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import com.my.instagram.domains.follow.repository.FollowRepository;
import com.my.instagram.domains.follow.service.FollowService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
public class followServiceTest {

    @Autowired
    FollowService followService;
    @Autowired
    FollowRepository followRepository;

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
    void 팔로잉조회(){
        List<FollowSearchResponse> test0 = followService.searchFollower("test0");
        int totalCount = test0.size();

        for (int i = 0; i < test0.size(); i++) {
            System.out.println(test0.get(i));
        }

        assertThat(totalCount).isEqualTo(9);
    }

    @Test
    @Rollback(false)
    void 팔로우삭제(){
        FollowDeleteRequest followDeleteRequest = new FollowDeleteRequest();
        followDeleteRequest.setProfileName("test0");
        followDeleteRequest.setFollowName("test2");

        followService.deleteFollow(followDeleteRequest);

        FollowSearchResponse data = followRepository.findByProfileNameAndFollowName("test0", "test1");

        assertThat(data.getFollowName()).isNull();
    }
}
