package com.my.instagram.common.file.dto.response;

import com.my.instagram.common.file.domain.Files;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class FileSearchResponse {
    private UUID fileName;
    private String realFileName;
    private String filePath;
    private String fileExt;
    private int fileSeq;

    public FileSearchResponse(Files files) {
        this.fileName     = files.getFileName();
        this.realFileName = files.getRealFileName();
        this.filePath     = files.getFilePath();
        this.fileExt      = files.getFileExt();
        this.fileSeq      = files.getFileSeq();
    }
}
