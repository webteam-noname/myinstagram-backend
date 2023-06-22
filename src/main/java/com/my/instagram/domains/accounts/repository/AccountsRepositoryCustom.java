package com.my.instagram.domains.accounts.repository;

import com.my.instagram.domains.accounts.dto.response.AccountsResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface AccountsRepositoryCustom {

    Slice<AccountsResponse> findAllSlice(Pageable pageable);
}
