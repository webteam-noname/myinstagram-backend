package com.my.instagram.domains.accounts.repository;

import com.my.instagram.domains.accounts.dto.response.AccountsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.criterion.Projection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.my.instagram.domains.accounts.domain.QAccounts.accounts;

@RequiredArgsConstructor
public class AccountsRepositoryCustomImpl implements AccountsRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<AccountsResponse> findAllSlice(Pageable pageable) {
        List<AccountsResponse> content = queryFactory.select(Projections.constructor(AccountsResponse.class,
                                                             accounts))
                                                     .from(accounts)
                                                     .where(accounts.checkAuthYn.eq('Y'))
                                                     .offset(pageable.getOffset())
                                                     .limit(pageable.getPageSize())
                                                     .fetch();

        JPAQuery<Long> count = queryFactory.select(accounts.count())
                                           .from(accounts);

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }
}
