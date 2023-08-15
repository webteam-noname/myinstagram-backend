package com.my.instagram.common.file.controller;

import com.my.instagram.common.dto.ApiResponse;
import com.my.instagram.common.file.dto.request.FileDeleteRequest;
import com.my.instagram.common.file.dto.request.FileUpdateRequest;
import com.my.instagram.common.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class FileController {

     private final FileService fileService;

     // 2023-08-14 주석
     // 현재의 협력을 표현할 때는 굳이 이 메서드를 사용할 필요가 없다.
     // 필요 없는 이유는 따로 파일 테이블 조회하는 경우는 없기 때문이다.
     /*@GetMapping("/api/files")
     public ApiResponse<FileSearchResponse> searchFile(@Valid @RequestBody FileSearchRequest fileSearchRequest){
         return new ApiResponse<>(HttpStatus.OK, fileService.searchFile(fileSearchRequest));
     }*/

     @PostMapping("/api/files")
     public ApiResponse<Long> saveFile(@RequestPart(required = false) MultipartFile file){
         return new ApiResponse<>(HttpStatus.OK, fileService.saveFile( file));
     }

     @PutMapping("/api/files")
     public ApiResponse<Long> updateFile(@Valid FileUpdateRequest fileUpdateRequest,
                                         @RequestPart(required = false) MultipartFile file){
        return new ApiResponse<>(HttpStatus.OK, fileService.updateFile(fileUpdateRequest, file));
     }

    @DeleteMapping("/api/files")
    public ApiResponse<String> deleteFile(@Valid @RequestBody FileDeleteRequest fileDeleteRequest){
        return new ApiResponse<>(HttpStatus.OK, fileService.deleteFile(fileDeleteRequest));
    }
}
