package com.my.instagram.domains.file.controller;

import com.my.instagram.common.dto.ApiResponse;
import com.my.instagram.domains.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FileController {

     private final FileService fileService;

     @GetMapping("/api/file")
     public ApiResponse<String> searchFile(){
         // fileService.searchFile()
         return new ApiResponse<>(HttpStatus.OK, "파일을 조회합니다.");
     }

     @PostMapping("/api/file")
     public ApiResponse<String> saveFile(){
         // fileService.saveFile();
         return new ApiResponse<>(HttpStatus.OK, "파일을 저장했습니다.");
     }

     @PutMapping("/api/file")
     public ApiResponse<String> updateFile(){
         // fileService.updateFile();
        return new ApiResponse<>(HttpStatus.OK, "파일을 수정했습니다.");
     }

    @DeleteMapping("/api/file")
    public ApiResponse<String> deleteFile(){
        // fileService.deleteFile();
        return new ApiResponse<>(HttpStatus.OK, "파일을 삭제했습니다.");
    }
}
