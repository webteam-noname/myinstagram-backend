package com.my.instagram.common.file.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
public class FileSearchRequest {

    @NotNull(message = "파일ID를 입력해주세요")
    private Long id;

    public FileSearchRequest(Long id) {
        this.id = id;
    }
}
