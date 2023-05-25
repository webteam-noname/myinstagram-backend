package com.my.instagram.domains.file.repository;

import com.my.instagram.domains.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File,Long> {
}
