package com.my.instagram.common.file.repository;

import com.my.instagram.common.file.domain.Files;
import com.my.instagram.common.file.dto.response.FileSearchResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<Files,Long> {

    public void deleteByIdAndFileNameAndFileSeq(Long id, UUID fileName, Integer fileSeq);

    public Files findByIdAndFileName(Long id, UUID fileName);
}


