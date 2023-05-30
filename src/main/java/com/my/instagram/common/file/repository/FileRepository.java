package com.my.instagram.common.file.repository;

import com.my.instagram.common.file.domain.Files;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<Files,Long> {

}


