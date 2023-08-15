package com.my.instagram.common.file.service;

import com.my.instagram.common.file.domain.Files;
import com.my.instagram.common.file.dto.request.FileDeleteRequest;
import com.my.instagram.common.file.dto.request.FileUpdateRequest;
import com.my.instagram.common.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileRepository fileRepository;

    // 2023-08-14 주석
    // 현재의 협력을 표현할 때는 굳이 이 메서드를 사용할 필요가 없다.
    // 필요 없는 이유는 따로 파일 테이블 조회하는 경우는 없기 때문이다.
    /*public FileSearchResponse searchFile(FileSearchRequest fileSearchRequest) {
        Files files = fileRepository.findById(fileSearchRequest.getId()).get();
        return new FileSearchResponse(files);
    }*/

    // 2023-08-14 파일 단건을 저장합니다.
    public void saveSingleFile(FileSaveType entity, MultipartFile file){
        Files fileEntity = new Files(file);
        fileRepository.save(fileEntity);
        fileEntity.saveSingleFile(entity, file);
    }

    // 2023-08-14 파일 단건을 수정합니다.
    public void updateSingleFile(Files fileEntity, MultipartFile file){
        fileEntity.updateSingleFile(file);
    }

    // 2023-08-14 파일 단건을 삭제합니다.
    public void deleteSingleFile(Files fileEntity){
        fileEntity.deleteSingleFile();
    }


    public void saveFileTest(FileSaveType fileSaveEntity, MultipartFile file) {

        //Files files = fileSaveEntity.getFiles();

        // saveFiles
        //if(files == null){

        // updateFiles
        //}else{
           /* // 기존 파일이 존재한다면 삭제합니다.
            if(StringUtils.hasText(files.getRealFileName())){
                files.deleteFileTest();
            }

            // file을 DB에 업데이트 합니다.
            Files files1 = new Files(files.getId(), file);

            // 파일을 업로드합니다.
            files1.uploadFile(file);*/
        //}


    }

    public void saveFileTest(FileSaveEntity function, MultipartFile file) {
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

        fileRepository.save(fileEntity);
        fileEntity.saveFileTest(function, file);
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

        if(StringUtils.hasText(files.getRealFileName())){
            deleteServerFile(files);
        }

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
        // files.deleteFile();
    }

}
