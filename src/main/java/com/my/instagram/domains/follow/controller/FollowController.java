package com.my.instagram.domains.follow.controller;

import com.my.instagram.common.dto.ApiResponse;
import com.my.instagram.domains.accounts.dto.response.ProfileUpdateResponse;
import com.my.instagram.domains.follow.dto.request.FollowBlockRequest;
import com.my.instagram.domains.follow.dto.request.FollowDeleteRequest;
import com.my.instagram.domains.follow.dto.request.FollowSaveRequest;
import com.my.instagram.domains.follow.dto.request.FollowSearchRequest;
import com.my.instagram.domains.follow.dto.response.FollowBlockResponse;
import com.my.instagram.domains.follow.dto.response.FollowDeleteResponse;
import com.my.instagram.domains.follow.dto.response.FollowSaveResponse;
import com.my.instagram.domains.follow.dto.response.FollowSearchResponse;
import com.my.instagram.domains.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @GetMapping("/api/follow")
    public ApiResponse<FollowSearchResponse> searchFollow(@Valid @RequestBody FollowSearchRequest followSearchRequest) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, followService.searchFollow(followSearchRequest));
    }

    @PostMapping("/api/follow")
    public ApiResponse<FollowSaveResponse> saveFollow(@Valid @RequestBody FollowSaveRequest followSaveRequest) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, followService.saveFollow(followSaveRequest));
    }

    @DeleteMapping("/api/follow")
    public ApiResponse<String> deleteFollow(@Valid @RequestBody FollowDeleteRequest followDeleteRequest) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, followService.deleteFollow(followDeleteRequest));
    }

    @PutMapping("/api/follow/block")
    public ApiResponse<String> blockFollow(@Valid @RequestBody FollowBlockRequest followBlockRequest) throws IOException {
        return new ApiResponse<>(HttpStatus.OK, followService.blockFollow(followBlockRequest));
    }
}
