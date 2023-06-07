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

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;

    public FollowSearchResponse searchFollow(FollowSearchRequest followSearchRequest) {
        return followRepository.findByUsername(followSearchRequest.getUsername());
    }

    public FollowSaveResponse saveFollow(FollowSaveRequest followSaveRequest) {
        Follow follow = Follow.builder()
                              .username(followSaveRequest.getUsername())
                              .follow(followSaveRequest.getFollow())
                              .blockYn('N')
                              .build();

        followRepository.save(follow);

        return new FollowSaveResponse(follow);
    }

    public String deleteFollow(FollowDeleteRequest followDeleteRequest) {
        Follow follow = followRepository.findByUsernameFollow(followDeleteRequest.getUsername(), followDeleteRequest.getFollow());
        followRepository.delete(follow);
        return "팔로워를 취소했습니다.";
    }

    public String blockFollow(FollowBlockRequest followBlockRequest) {
        Follow follow = followRepository.findByUsernameFollow(followBlockRequest.getUsername(), followBlockRequest.getFollow());
        followRepository.blockFollow(follow.getUsername(),follow.getFollow(),follow.getBlockYn());
        return "정상처리되었습니다.";
    }
}
