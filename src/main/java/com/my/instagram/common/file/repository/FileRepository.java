package com.my.instagram.common.file.repository;

import com.my.instagram.common.file.domain.Files;
import com.my.instagram.common.file.dto.response.FileSearchResponse;
import com.querydsl.collections.CollQueryFunctions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<Files,Long> {

}


