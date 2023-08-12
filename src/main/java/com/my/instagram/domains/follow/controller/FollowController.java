package com.my.instagram.domains.follow.controller;

import com.my.instagram.common.dto.ApiResponse;
import com.my.instagram.domains.follow.dto.request.FollowApproveRequest;
import com.my.instagram.domains.follow.dto.request.FollowBlockRequest;
import com.my.instagram.domains.follow.dto.request.FollowDeleteRequest;
import com.my.instagram.domains.follow.dto.request.FollowSaveRequest;
import com.my.instagram.domains.follow.dto.response.FollowSaveResponse;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import com.my.instagram.domains.follow.dto.response.FollowingSearchResponse;
import com.my.instagram.domains.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @GetMapping("/api/follows/{profileName}")
    public ApiResponse<List<FollowSearchResponse>> searchFollow(@PathVariable("profileName") String profileName) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, followService.searchFollow(profileName));
    }

    // 2023-08-08 변경 사항
    // followers -> following으로 변경, 메서드 명 변경: searchFollower -> searchFollowing
    // dto명 변경: FollowSearchResponse -> FollowingSearchResponse
    @GetMapping("/api/following/{profileName}")
    public ApiResponse<List<FollowingSearchResponse>> searchFollowing(@PathVariable("profileName") String profileName) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, followService.searchFollowing(profileName));
    }

    @PostMapping("/api/follows/{profileName}")
    public ApiResponse<FollowSaveResponse> saveFollow(@Valid @RequestBody FollowSaveRequest followSaveRequest) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, followService.saveFollow(followSaveRequest));
    }

    @DeleteMapping("/api/follows/{profileName}")
    public ApiResponse<String> deleteFollow(@Valid @RequestBody FollowDeleteRequest followDeleteRequest) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, followService.deleteFollow(followDeleteRequest));
    }

    @PutMapping("/api/follows/{profileName}/approval")
    public ApiResponse<String> approveFollow(@Valid @RequestBody FollowApproveRequest followApproveRequest) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, followService.approveFollow(followApproveRequest));
    }

    @PutMapping("/api/follows/{profileName}/block")
    public ApiResponse<String> blockFollow(@Valid @RequestBody FollowBlockRequest followBlockRequest) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, followService.blockFollow(followBlockRequest));
    }
}
