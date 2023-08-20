package com.my.instagram.domains.follow.service;

import com.my.instagram.common.file.domain.Files;
import com.my.instagram.common.file.repository.FileRepository;
import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.accounts.repository.AccountsRepository;
import com.my.instagram.domains.follow.domain.Follow;
import com.my.instagram.domains.follow.dto.request.FollowApproveRequest;
import com.my.instagram.domains.follow.dto.request.FollowBlockRequest;
import com.my.instagram.domains.follow.dto.request.FollowDeleteRequest;
import com.my.instagram.domains.follow.dto.request.FollowSaveRequest;
import com.my.instagram.domains.follow.dto.response.FollowSaveResponse;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import com.my.instagram.domains.follow.dto.response.FollowingSearchResponse;
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
    private final FollowRepository followRepository;
    private final AccountsRepository accountsRepository;

    public List<FollowingSearchResponse> searchFollowing(String profileName) {
        List<FollowingSearchResponse> followingByUsername = followRepository.findFollowingByUsername(profileName);
        existDataSkipElseException(followingByUsername.size());
        return followingByUsername;
    }

    public List<FollowSearchResponse> searchFollow(String profileName) {
        List<FollowSearchResponse> followByUsername = followRepository.findFollowByUsername(profileName);
        existDataSkipElseException(followByUsername.size());
        return followByUsername;
    }

    // 2023-08-12 검토 사항
    // 1. Exception 처리가 너무 일반적으로 String의 내용을 포함하고 있다.
    //    그렇기에 Exception의 내용을 정리한 별도의 클래스가 하나만 있으면 좋겠다.
    // 2. Exception 명명 규칙을 정해서 만들었으면 좋겠음
    private void existDataSkipElseException(int userCount) {
        if (userCount == 0) {
            throw new RuntimeException("팔로우 조회를 할 수 없습니다.");
        }
    }

    public FollowSaveResponse saveFollow(FollowSaveRequest followSaveRequest) {
        Accounts accounts = getAccounts(followSaveRequest.getProfileName());
        Accounts followAccount = getAccounts(followSaveRequest.getFollowName());

        followOverTwiceExistsException(accounts.getId(), followSaveRequest.getFollowName());

        Follow follow = Follow.builder()
                              .accounts(accounts)
                              .followAccounts(followAccount)
                              .blockYn('N')
                              .followAccept('N')
                              .build();

        followRepository.save(follow);

        return new FollowSaveResponse(follow);
    }

    private void followOverTwiceExistsException(Long accountsId, String followName) {
        if(followRepository.countByAccountsIdAndFollowName(accountsId, followName) > 0){
            throw new RuntimeException("Follow는 중복될 수 없습니다.");
        }
    }

    // 2023-08-12 검토사항
    // 1. return 조회시 accountsRepository가 아닌 accountsService에 하나만 정의해서 사용해도 되지 않을까?
    private Accounts getAccounts(String profileName) {
        if(accountNameExists(profileName)){
            return accountsRepository.findByProfileName(profileName).orElseThrow(() -> new RuntimeException("유저를 조회할 수 없습니다."));
        }
        return null;
    }

    private boolean accountNameExists(String name) {
        return StringUtils.hasText(name);
    }

    // 2023-08-08 변경 사항
    // 아래 코드 주석 처리하였음
    public String deleteFollow(FollowDeleteRequest followDeleteRequest) {
        Follow follow = followRepository.findAcceptByProfileNameAndFollowName(followDeleteRequest.getProfileName(), followDeleteRequest.getFollowName(), 'Y');
        followRepository.deleteByAccountsIdAndFollowAccountsId(follow.getAccounts().getId(), follow.getFollowAccounts().getId());

        return "팔로워를 취소했습니다.";
    }


    public String blockFollow(FollowBlockRequest followBlockRequest) {
        Follow follow = followRepository.findByProfileNameAndFollowName(followBlockRequest.getProfileName(), followBlockRequest.getFollowName());
        follow.setBlockYn(followBlockRequest.getBlockYn());
        return "정상처리되었습니다.";
    }


    public String approveFollow(FollowApproveRequest followApproveRequest) {
        Follow follow = followRepository.findAcceptByProfileNameAndFollowName(followApproveRequest.getProfileName(), followApproveRequest.getFollowName(),'N');
        follow.setFollowAccept(followApproveRequest.getFollowAccept());
        return "정상처리되었습니다.";
    }
}
