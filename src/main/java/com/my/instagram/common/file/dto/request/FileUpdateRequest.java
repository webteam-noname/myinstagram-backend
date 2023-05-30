package com.my.instagram.common.file.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class FileUpdateRequest {

    @NotNull(message = "파일ID를 입력해주세요")
    private Long   id;

    public FileUpdateRequest(Long id) {
        this.id = id;
    }
}
