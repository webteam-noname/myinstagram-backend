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
    private final FileRepository fileRepository;

    public Long searchFollowCount(String profileName) {
        return followRepository.countFollowByUsername(profileName);
    }

    public List<FollowSearchResponse> searchFollower(String profileName) {
        List<FollowSearchResponse> followerByUsername = followRepository.findFollowerByUsername(profileName);
        existDataSkipElseException(followerByUsername);
        inputFollowerFileImg(followerByUsername);
        return followerByUsername;
    }

    private void inputFollowFileImg(List<FollowSearchResponse> listData) {
        int dataLength = listData.size();
        for (int i = 0; i < dataLength; i++) {
             if(listData.get(i).getProfileImgFileId() != null){
                 Accounts accounts = accountsRepository.findByProfileName(listData.get(i).getFollowName()).get();
                 Files files = fileRepository.findById(accounts.getProfileImgFileId()).get();
                 listData.get(i).setProfileImg(files.getFilePath()+files.getFileName()+"."+files.getFileExt());
             }
        }
    }

    private void inputFollowerFileImg(List<FollowSearchResponse> listData) {
        int dataLength = listData.size();
        for (int i = 0; i < dataLength; i++) {
            if(listData.get(i).getProfileImgFileId() != null){
                Accounts accounts = accountsRepository.findByProfileName(listData.get(i).getProfileName()).get();
                Files files = fileRepository.findById(accounts.getProfileImgFileId()).get();
                listData.get(i).setProfileImg(files.getFilePath()+files.getFileName()+"."+files.getFileExt());
            }
        }
    }

    public Long searchFollowerCount(String profileName) {
        return followRepository.countFollowerByUsername(profileName);
    }

    public List<FollowSearchResponse> searchFollow(String profileName) {
        List<FollowSearchResponse> followByUsername = followRepository.findFollowByUsername(profileName);
        existDataSkipElseException(followByUsername);
        inputFollowFileImg(followByUsername);
        return followByUsername;
    }

    private void existDataSkipElseException(List<FollowSearchResponse> followByUsername) {
        if (followByUsername.size() == 0) {
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

    private Accounts getAccounts(String profileName) {
        if(accountNameExists(profileName)){
            return accountsRepository.findByProfileName(profileName).orElseThrow(() -> new RuntimeException("유저를 조회할 수 없습니다."));
        }
        return null;
    }

    private boolean accountNameExists(String name) {
        return StringUtils.hasText(name);
    }

    public String deleteFollow(FollowDeleteRequest followDeleteRequest) {
        Follow follow = followRepository.findAcceptByProfileNameAndFollowName(followDeleteRequest.getProfileName(), followDeleteRequest.getFollowName(), 'Y');
        followRepository.deleteByAccountsIdAndFollowAccountsId(follow.getAccounts().getId(), follow.getFollowAccounts().getId());

        System.out.println(followDeleteRequest.getFollowName());
        System.out.println(followDeleteRequest.getProfileName());
        Follow oppositeFollow = followRepository.findAcceptByProfileNameAndFollowName(followDeleteRequest.getFollowName(), followDeleteRequest.getProfileName(),'Y');

        if(oppositeFollow != null){
            followRepository.deleteByAccountsIdAndFollowAccountsId(oppositeFollow.getAccounts().getId(), oppositeFollow.getFollowAccounts().getId());
        }

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
