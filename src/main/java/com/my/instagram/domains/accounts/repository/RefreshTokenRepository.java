package com.my.instagram.domains.accounts.repository;


import com.my.instagram.domains.accounts.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
}
