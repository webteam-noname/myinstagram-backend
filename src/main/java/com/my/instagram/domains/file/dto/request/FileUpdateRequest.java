package com.my.instagram.domains.file.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class FileUpdateRequest {

    @NotNull(message = "파일ID를 입력해주세요")
    private Long id;
    private String realFileName;
    private String realFilePath;
    private String realFileExt;
}
