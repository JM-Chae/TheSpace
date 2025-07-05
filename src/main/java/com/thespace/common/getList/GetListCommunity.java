package com.thespace.common.getList;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.thespace.spaceweb.community.Community;
import com.thespace.spaceweb.community.CommunityDTOs.Info;
import com.thespace.spaceweb.community.QCommunity;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;

@Component
public class GetListCommunity extends QuerydslRepositorySupport {

    public GetListCommunity() {
        super(Community.class);
    }

    public Page<Info> getList(String[] types, String keyword, Pageable pageable) {
        QCommunity community = QCommunity.community;

        JPQLQuery<Community> communityJPQLQuery = from(community);
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

        communityJPQLQuery.where(booleanBuilder);

        communityJPQLQuery.distinct();
        communityJPQLQuery.groupBy(community);
        getQuerydsl().applyPagination(pageable, communityJPQLQuery);
        JPQLQuery<Community> selectQuery = communityJPQLQuery.select(community);
        List<Community> communityList = selectQuery.fetch();

        List<Info> dtoList = communityList.stream().map(community1 ->
        Info.builder()
                .id(community1.getId())
                .name(community1.getName())
                .build())
            .toList();

        long totalCount = selectQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }
}
