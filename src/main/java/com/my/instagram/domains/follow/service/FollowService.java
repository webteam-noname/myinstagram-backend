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
import java.util.Optional;

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
        List<FollowSearchResponse> followerByUsername = followRepository.findFollowerByUsername(profileName);
        existDataSkipElseException(followerByUsername);
        return followerByUsername;
    }

    public Long searchFollowerCount(String profileName) {
        return followRepository.countFollowerByUsername(profileName);
    }

    public List<FollowSearchResponse> searchFollow(String profileName) {
        List<FollowSearchResponse> followByUsername = followRepository.findFollowByUsername(profileName);
        existDataSkipElseException(followByUsername);
        return followByUsername;
    }

    private void existDataSkipElseException(List<FollowSearchResponse> followByUsername) {
        if (followByUsername.size() == 0) {
            throw new RuntimeException("팔로우 조회를 할 수 없습니다.");
        }
    }

    public FollowSaveResponse saveFollow(FollowSaveRequest followSaveRequest) {
        Accounts accounts = getAccounts(followSaveRequest.getProfileName());

        followOverTwiceExistsException(accounts.getId(), followSaveRequest.getFollowName());

        Follow follow = Follow.builder()
                              .accounts(accounts)
                              .followName(followSaveRequest.getFollowName())
                              .blockYn('N')
                              .build();

        followRepository.save(follow);

        return new FollowSaveResponse(follow);
    }

    private void followOverTwiceExistsException(Long accountsId, String followName) {
        if(followRepository.countByAccountsIdAndFollowName(accountsId, followName) > 0){
            throw new RuntimeException("Follow는 중복될 수 없습니다.");
        }
    }

    private Accounts getAccounts(String profileName) {
        if(accountNameExists(profileName)){
            return accountsRepository.findByProfileName(profileName).orElseThrow(() -> new RuntimeException("조회된 데이터가 없습니다."));
        }
        return null;
    }

    private boolean accountNameExists(String name) {
        return StringUtils.hasText(name);
    }

    public String deleteFollow(FollowDeleteRequest followDeleteRequest) {
        Accounts accounts = getAccounts(followDeleteRequest.getProfileName());
        followRepository.deleteByAccountsIdAndFollowName(accounts.getId(), followDeleteRequest.getFollowName());
        return "팔로워를 취소했습니다.";
    }

    public String blockFollow(FollowBlockRequest followBlockRequest) {
        FollowSearchResponse follow = followRepository.findByProfileNameAndFollowName(followBlockRequest.getProfileName(), followBlockRequest.getFollowName());


        followRepository.blockFollow(follow.getAccountId(), follow.getFollowName(), followBlockRequest.getBlockYn());
        return "정상처리되었습니다.";
    }



}
