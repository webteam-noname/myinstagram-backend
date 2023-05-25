package com.my.instagram.domains.file.domain;

import com.my.instagram.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    private String realFileName;
    private String realFilePath;
    private String realFileExt;
    private int fileSeq;

    @Builder
    public File(String realFileName, String realFilePath, String realFileExt, int fileSeq) {
        this.realFileName = realFileName;
        this.realFilePath = realFilePath;
        this.realFileExt = realFileExt;
        this.fileSeq = fileSeq;
    }
}
