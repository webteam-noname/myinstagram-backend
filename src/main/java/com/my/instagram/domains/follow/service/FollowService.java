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
    private final FileRepository fileRepository;

    /*public Long searchFollowCount(String profileName) {
        return followRepository.countFollowByUsername(profileName);
    }*/

    // 2023-08-08 변경 사항
    // 메서드 명 변경: searchFollower -> searchFollowing, 관련 테스트 코드 수정
    // 변수명 변경: followerByUsername -> followingByUsername
    // Repository 메서드명 변경: findFollowerByUsername -> findFollowingByUsername
    // 메서드명 변경: inputFollowerFileImg -> inputFollowingFileImg
    // DTO명 변경: FollowSearchResponse -> FollowingSearchResponse
    // 임시로 existDataSkipElseException 파라미터 수정
    // 다음에는 공통 메서드에 대해서 파라미터를 DTO로 넘겨주는 작업은 지양한다.
    public List<FollowingSearchResponse> searchFollowing(String profileName) {
        List<FollowingSearchResponse> followingByUsername = followRepository.findFollowingByUsername(profileName);
        existDataSkipElseException(followingByUsername.size());
        inputFollowingFileImg(followingByUsername);
        return followingByUsername;
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

    // 2023-08-08 변경 사항
    // 메서드명 변경: inputFollowerFileImg -> inputFollowingFileImg
    // DTO명 변경: FollowSearchResponse -> FollowingSearchResponse
    private void inputFollowingFileImg(List<FollowingSearchResponse> listData) {
        int dataLength = listData.size();
        for (int i = 0; i < dataLength; i++) {
            if(listData.get(i).getProfileImgFileId() != null){
                Accounts accounts = accountsRepository.findByProfileName(listData.get(i).getProfileName()).get();
                Files files = fileRepository.findById(accounts.getProfileImgFileId()).get();
                listData.get(i).setProfileImg(files.getFilePath()+files.getFileName()+"."+files.getFileExt());
            }
        }
    }

    /*public Long searchFollowerCount(String profileName) {
        return followRepository.countFollowerByUsername(profileName);
    }*/

    public List<FollowSearchResponse> searchFollow(String profileName) {
        List<FollowSearchResponse> followByUsername = followRepository.findFollowByUsername(profileName);
        existDataSkipElseException(followByUsername.size());
        inputFollowFileImg(followByUsername);
        return followByUsername;
    }

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

        // 2023-08-08: 맞팔되어 있는 대상 중 한명이라도 삭제하면 둘다 삭제된다고 생각했는데 그것은 아니어서 주석 처리함
        /*Follow oppositeFollow = followRepository.findAcceptByProfileNameAndFollowName(followDeleteRequest.getFollowName(), followDeleteRequest.getProfileName(),'Y');

        if(oppositeFollow != null){
            followRepository.deleteByAccountsIdAndFollowAccountsId(oppositeFollow.getAccounts().getId(), oppositeFollow.getFollowAccounts().getId());
        }*/

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
