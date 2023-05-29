package com.my.instagram.common.file.service;

import com.my.instagram.common.file.domain.Files;
import com.my.instagram.common.file.dto.request.FileDeleteRequest;
import com.my.instagram.common.file.dto.request.FileSearchRequest;
import com.my.instagram.common.file.dto.request.FileUpdateRequest;
import com.my.instagram.common.file.dto.response.FileSearchResponse;
import com.my.instagram.common.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public FileSearchResponse searchFile(FileSearchRequest fileSearchRequest) {
        Files files = fileRepository.findById(fileSearchRequest.getId()).get();
        return new FileSearchResponse(files);
    }

    public Long saveFile(List<MultipartFile> files) {
        Files fileEntity = null;
        for (MultipartFile file : files) {
            UUID fileName       = UUID.randomUUID();
            String realFileName = file.getOriginalFilename();
            String filePath     = "c:/files/";

            fileEntity = Files.builder()
                              .filePath(filePath)
                              .fileName(fileName)
                              .realFileName(file.getOriginalFilename())
                              .fileExt(realFileName.substring(realFileName.lastIndexOf(".") + 1))
                              .fileSeq(searchFileSeq())
                              .build();

            fileEntity.saveFile(file);
            fileRepository.save(fileEntity);
        }

        return fileEntity.getId();
    }

    private int searchFileSeq() {
        return 0;
    }

    public Long updateFile(FileUpdateRequest fileUpdateRequest, List<MultipartFile> files) {
        Files byIdFileName = fileRepository.findByIdAndFileName(fileUpdateRequest.getId(), fileUpdateRequest.getFileName());

        deleteFile(new FileDeleteRequest(byIdFileName.getId(),
                                         byIdFileName.getFilePath(),
                                         byIdFileName.getFileName(),
                                         byIdFileName.getFileSeq()));

        saveFile(files);

        return byIdFileName.getId();
    }


    public String deleteFile(FileDeleteRequest fileDeleteRequest) {
        Files files = new Files(fileDeleteRequest.getId(), fileDeleteRequest.getFilePath(), fileDeleteRequest.getFileName());

        fileRepository.deleteByIdAndFileNameAndFileSeq(fileDeleteRequest.getId(),
                                                         fileDeleteRequest.getFileName(),
                                                         fileDeleteRequest.getFileSeq());


        files.deleteFile();

        return "파일을 삭제했습니다.";
    }
}
