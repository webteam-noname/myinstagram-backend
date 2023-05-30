package com.my.instagram.common.file.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class FileDeleteRequest {

    @NotNull(message = "파일ID를 입력해주세요")
    private Long id;

    public FileDeleteRequest(Long id) {
        this.id = id;
    }
}
