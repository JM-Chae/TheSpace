package com.thespace.common.getList;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thespace.spaceweb.community.Community;
import com.thespace.spaceweb.community.CommunityDTOs.Info;
import com.thespace.spaceweb.community.QCommunity;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetListCommunity {

    private final JPAQueryFactory queryFactory;

    public Page<Info> getList(String[] types, String keyword, Pageable pageable) {
        QCommunity community = QCommunity.community;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if ((types != null && types.length > 0) && keyword != null) {
//            for (String type : types) Currently, only one type is allowed.
//              {
            if (types[0].equals("n")) {
                booleanBuilder.or(community.name.contains(keyword));
            }
            if (types[0].equals("i")) {
                Arrays.stream(keyword.split(",")).toList()
                    .forEach(id -> booleanBuilder.or(community.id.eq(Long.parseLong(id))));
            }
//              }
        }

        JPAQuery<Community> query = queryFactory
            .selectFrom(community)
            .where(booleanBuilder)
            .distinct();

        List<Community> communityList = QuerydslUtils.applyPagination(query, pageable, community)
            .fetch();

        List<Info> dtoList = communityList.stream().map(community1 ->
        Info.builder()
                .id(community1.getId())
                .name(community1.getName())
                .build())
            .toList();

        long totalCount = Optional.ofNullable(queryFactory
            .select(community.countDistinct())
            .from(community)
            .where(booleanBuilder)
            .fetchOne())
            .orElse(0L);

        return new PageImpl<>(dtoList, pageable, totalCount);
    }
}
