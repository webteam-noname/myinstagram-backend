package com.my.instagram.domains.follow.service;

import com.my.instagram.domains.follow.domain.Follow;
import com.my.instagram.domains.follow.dto.request.FollowBlockRequest;
import com.my.instagram.domains.follow.dto.request.FollowDeleteRequest;
import com.my.instagram.domains.follow.dto.request.FollowSaveRequest;
import com.my.instagram.domains.follow.dto.request.FollowSearchRequest;
import com.my.instagram.domains.follow.dto.response.FollowBlockResponse;
import com.my.instagram.domains.follow.dto.response.FollowDeleteResponse;
import com.my.instagram.domains.follow.dto.response.FollowSaveResponse;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import com.my.instagram.domains.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;

    public Long searchFollowCount(FollowSearchRequest followSearchRequest) {
        return followRepository.countFollowByUsername(followSearchRequest.getUsername());
    }

    public List<FollowSearchResponse> searchFollower(FollowSearchRequest followSearchRequest) {
        return followRepository.findFollowerByUsername(followSearchRequest.getUsername());
    }

    public Long searchFollowerCount(FollowSearchRequest followSearchRequest) {
        return followRepository.countFollowerByUsername(followSearchRequest.getUsername());
    }

    public List<FollowSearchResponse> searchFollow(FollowSearchRequest followSearchRequest) {
        return followRepository.findFollowByUsername(followSearchRequest.getUsername());
    }

    public FollowSaveResponse saveFollow(FollowSaveRequest followSaveRequest) {
        Follow follow = Follow.builder()
                              .username(followSaveRequest.getUsername())
                              .followName(followSaveRequest.getFollowName())
                              .blockYn('N')
                              .build();

        followRepository.save(follow);

        return new FollowSaveResponse(follow);
    }

    public String deleteFollow(FollowDeleteRequest followDeleteRequest) {
        followRepository.deleteByUsernameAndFollowName(followDeleteRequest.getUsername(), followDeleteRequest.getFollowName());
        return "팔로워를 취소했습니다.";
    }

    public String blockFollow(FollowBlockRequest followBlockRequest) {
        FollowSearchResponse follow = followRepository.findByUsernameAndFollowName(followBlockRequest.getUsername(), followBlockRequest.getFollowName());

        followRepository.blockFollow(follow.getUsername(), follow.getFollowName(), followBlockRequest.getBlockYn());
        return "정상처리되었습니다.";
    }



}
