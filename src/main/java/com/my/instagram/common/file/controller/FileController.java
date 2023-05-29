package com.my.instagram.common.file.controller;

import com.my.instagram.common.dto.ApiResponse;
import com.my.instagram.common.file.dto.request.FileDeleteRequest;
import com.my.instagram.common.file.dto.request.FileSearchRequest;
import com.my.instagram.common.file.dto.request.FileUpdateRequest;
import com.my.instagram.common.file.dto.response.FileSearchResponse;
import com.my.instagram.common.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {

     private final FileService fileService;

     @GetMapping("/api/file")
     public ApiResponse<FileSearchResponse> searchFile(@Valid @RequestBody FileSearchRequest fileSearchRequest){
         return new ApiResponse<>(HttpStatus.OK, fileService.searchFile(fileSearchRequest));
     }

     @PostMapping("/api/file")
     public ApiResponse<Long> saveFile(@RequestPart(required = false) List<MultipartFile> files){
         return new ApiResponse<>(HttpStatus.OK, fileService.saveFile(files));
     }

     @PutMapping("/api/file")
     public ApiResponse<Long> updateFile(@Valid @RequestBody FileUpdateRequest fileUpdateRequest,
                                         @RequestPart(required = false) List<MultipartFile> files){
        return new ApiResponse<>(HttpStatus.OK, fileService.updateFile(fileUpdateRequest, files));
     }

    @DeleteMapping("/api/file")
    public ApiResponse<String> deleteFile(@Valid @RequestBody FileDeleteRequest fileDeleteRequest){
        return new ApiResponse<>(HttpStatus.OK, fileService.deleteFile(fileDeleteRequest));
    }
}
