package com.my.instagram.common.file.service;

import com.my.instagram.common.file.domain.FileSaveType;
import com.my.instagram.common.file.domain.Files;
import com.my.instagram.common.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileRepository fileRepository;

    public void saveSingleFile(FileSaveType entity, MultipartFile file){
        Files fileEntity = new Files(file);
        fileRepository.save(fileEntity);
        fileEntity.saveSingleFile(entity, file);
    }

    public void updateSingleFile(Files fileEntity, MultipartFile file){
        fileEntity.updateSingleFile(file);
    }

    public void deleteSingleFile(Files fileEntity){
        fileEntity.deleteSingleFile();
    }

}
