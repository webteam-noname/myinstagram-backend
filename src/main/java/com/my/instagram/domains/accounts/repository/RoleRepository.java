package com.my.instagram.domains.accounts.repository;

import com.my.instagram.domains.accounts.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    public Optional<Role> findByType(String roleAccounts);

}
