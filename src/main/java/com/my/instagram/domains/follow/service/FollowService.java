package com.my.instagram.domains.follow.service;

import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.accounts.repository.AccountsRepository;
import com.my.instagram.domains.follow.domain.Follow;
import com.my.instagram.domains.follow.dto.request.FollowBlockRequest;
import com.my.instagram.domains.follow.dto.request.FollowDeleteRequest;
import com.my.instagram.domains.follow.dto.request.FollowSaveRequest;
import com.my.instagram.domains.follow.dto.response.FollowSaveResponse;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import com.my.instagram.domains.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository   followRepository;
    private final AccountsRepository accountsRepository;

    public Long searchFollowCount(String profileName) {
        return followRepository.countFollowByUsername(profileName);
    }

    public List<FollowSearchResponse> searchFollower(String profileName) {
        return followRepository.findFollowerByUsername(profileName);
    }

    public Long searchFollowerCount(String profileName) {
        return followRepository.countFollowerByUsername(profileName);
    }

    public List<FollowSearchResponse> searchFollow(String profileName) {
        return followRepository.findFollowByUsername(profileName);
    }

    public FollowSaveResponse saveFollow(FollowSaveRequest followSaveRequest) {
        followOverTwiceExistsException(followSaveRequest.getProfileName(), followSaveRequest.getFollowName());

        Accounts accounts = getAccounts(followSaveRequest);

        Follow follow = Follow.builder()
                              .accounts(accounts)
                              .followName(followSaveRequest.getFollowName())
                              .blockYn('N')
                              .build();

        followRepository.save(follow);

        return new FollowSaveResponse(follow);
    }

    private void followOverTwiceExistsException(String profileName, String followName) {
        if(followRepository.countByProfileNameAndFollowName(profileName, followName) > 1){
            new RuntimeException("Follow는 중복될 수 없습니다.");
        }
    }

    private Accounts getAccounts(FollowSaveRequest followSaveRequest) {

        if(accountNameExists(followSaveRequest.getProfileName())){
            return accountsRepository.findByProfileName(followSaveRequest.getProfileName()).orElseThrow(() -> new RuntimeException("조회된 데이터가 없습니다."));
        }

        if(accountNameExists(followSaveRequest.getUsername())){
            return accountsRepository.findByUsername(followSaveRequest.getUsername()).orElseThrow(() -> new RuntimeException("조회된 데이터가 없습니다."));
        }

        return null;
    }

    private boolean accountNameExists(String name) {
        return StringUtils.hasText(name);
    }

    public String deleteFollow(FollowDeleteRequest followDeleteRequest) {
        followRepository.deleteByProfileNameAndFollowName(followDeleteRequest.getProfileName(), followDeleteRequest.getFollowName());
        return "팔로워를 취소했습니다.";
    }

    public String blockFollow(FollowBlockRequest followBlockRequest) {
        FollowSearchResponse follow = followRepository.findByUsernameAndFollowName(followBlockRequest.getUsername(), followBlockRequest.getFollowName());

        followRepository.blockFollow(follow.getUsername(), follow.getFollowName(), followBlockRequest.getBlockYn());
        return "정상처리되었습니다.";
    }



}
