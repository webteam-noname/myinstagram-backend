package com.my.instagram.domains.file.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileSaveRequest {

    private String realFileName;
    private String realFilePath;
    private String realFileExt;
}
