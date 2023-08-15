package com.my.instagram.common.file.domain;

import com.my.instagram.common.domain.BaseEntity;
import com.my.instagram.common.file.service.FileSaveType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Files extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(unique = true)
    private UUID fileName;
    private String realFileName;
    private String filePath;
    private String fileExt;
    private int fileSeq;

    public Files(Long fileId, MultipartFile file){
        this(fileId,
             UUID.randomUUID(),
             file.getOriginalFilename(),
             "c:/files/",
             file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1),
             0);
    }

    public Files(MultipartFile file){
        this(null,
                UUID.randomUUID(),
                file.getOriginalFilename(),
                "c:/files/",
                file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1),
                0);
    }

    /*public void saveFileTest(FileSaveType entity, MultipartFile file) {
        uploadFile(file);
        entity.saveFiles(this);
    }*/

    public void saveSingleFile(FileSaveType entity, MultipartFile file) {
        uploadFile(file);
        entity.saveFiles(this);
    }

    public void updateSingleFile(MultipartFile file) {
        if(this.id == null){
            throw new RuntimeException("파일 엔티티가 없어 수정할 수 없습니다.");
        }

        if(StringUtils.hasText(this.getRealFileName())){
            deleteSingleFile();
        }

        UUID fileName       = UUID.randomUUID();
        String realFileName = file.getOriginalFilename();
        String filePath     = "c:/files/";
        String fileExt      = realFileName.substring(realFileName.lastIndexOf(".") + 1);

        this.fileName     = fileName;
        this.realFileName = realFileName;
        this.filePath     = filePath;
        this.fileExt      = fileExt;

        uploadFile(file);
    }

    public String getImageFile(){
        if(this.fileName == null){
            return "no-image.jpg";
        }else{
            return this.filePath+ this.fileName+"."+this.fileExt;
        }
    }

    public void deleteSingleFile() {
        File file = new File(getImageFile());

        if(file.exists()){
            file.delete();
        }else{
            throw new RuntimeException("서버 파일 삭제가 완료되지 않았습니다.");
        }
    }

    public void uploadFile(MultipartFile file){
        if(file == null){
            return;
        }

        File dest = new File(this.filePath+this.fileName+"."+this.fileExt);

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드를 실패했습니다.");
        }
    }

    @Builder
    public Files(Long id, UUID fileName, String realFileName, String filePath, String fileExt, int fileSeq) {
        this.id           = id;
        this.fileName     = fileName;
        this.realFileName = realFileName;
        this.filePath     = filePath;
        this.fileExt      = fileExt;
        this.fileSeq      = fileSeq;
    }

    public Files(String filePath, UUID fileName, String fileExt) {
        this(null, fileName,null,filePath,fileExt,0);
    }

    public void updateFile(MultipartFile file) {
        if(file == null){
            this.fileName     = null;
            this.realFileName = null;
            this.filePath     = null;
            this.fileExt      = null;
        }else{
            UUID fileName       = UUID.randomUUID();
            String realFileName = file.getOriginalFilename();
            String filePath     = "c:/files/";
            String fileExt      = realFileName.substring(realFileName.lastIndexOf(".") + 1);

            this.fileName     = fileName;
            this.realFileName = realFileName;
            this.filePath     = filePath;
            this.fileExt      = fileExt;
        }
    }

    public void saveFile(MultipartFile file) {
        if(file == null){
            return;
        }

        File dest = new File(this.filePath+this.fileName+"."+this.fileExt);

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드를 실패했습니다.");
        }

    }

    public void deleteFile() {
        System.out.println(this.filePath+ this.fileName+"."+this.fileExt);
        File file = new File(this.filePath+ this.fileName+"."+this.fileExt);

        if(file.exists()){
            file.delete();
        }else{
            throw new RuntimeException("서버 파일 삭제가 완료되지 않았습니다.");
        }
    }


}
