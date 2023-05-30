package com.my.instagram.common.file.service;

import com.my.instagram.common.file.domain.Files;
import com.my.instagram.common.file.dto.request.FileDeleteRequest;
import com.my.instagram.common.file.dto.request.FileSearchRequest;
import com.my.instagram.common.file.dto.request.FileUpdateRequest;
import com.my.instagram.common.file.dto.response.FileSearchResponse;
import com.my.instagram.common.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileRepository fileRepository;

    public FileSearchResponse searchFile(FileSearchRequest fileSearchRequest) {
        Files files = fileRepository.findById(fileSearchRequest.getId()).get();
        return new FileSearchResponse(files);
    }

    public Long saveFile(MultipartFile file) {
        UUID fileName       = UUID.randomUUID();
        String realFileName = file.getOriginalFilename();
        String filePath     = "c:/files/";
        String fileExt      = realFileName.substring(realFileName.lastIndexOf(".") + 1);

        Files fileEntity = Files.builder()
                          .filePath(filePath)
                          .fileName(fileName)
                          .realFileName(file.getOriginalFilename())
                          .fileExt(fileExt)
                          .fileSeq(searchFileSeq())
                          .build();

        fileEntity.saveFile(file);
        fileRepository.save(fileEntity);

        return fileEntity.getId();
    }

    private int searchFileSeq() {
        return 0;
    }

    public Long updateFile(FileUpdateRequest fileUpdateRequest, MultipartFile file) {
        Files files = fileRepository.findById(fileUpdateRequest.getId()).get();

        deleteServerFile(files);
        files.updateFile(file);
        files.saveFile(file);

        return files.getId();
    }


    public String deleteFile(FileDeleteRequest fileDeleteRequest) {
        Files files = fileRepository.findById(fileDeleteRequest.getId()).get();
        deleteServerFile(files);
        fileRepository.deleteById(fileDeleteRequest.getId());

        return "파일을 삭제했습니다.";
    }

    private void deleteServerFile(Files fileById) {
        Files files = new Files(fileById.getFilePath(), fileById.getFileName(), fileById.getFileExt());
        files.deleteFile();
    }


}
