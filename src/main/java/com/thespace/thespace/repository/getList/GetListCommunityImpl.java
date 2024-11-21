package com.thespace.thespace.repository.getList;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.domain.QCommunity;
import com.thespace.thespace.dto.CommunityDTO;
import com.thespace.thespace.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Slf4j
public class GetListCommunityImpl extends QuerydslRepositorySupport implements GetListCommunity
  {

    public GetListCommunityImpl(CategoryRepository categoryRepository)
      {
        super(Community.class);
      }

    public Page<CommunityDTO> getList(String[] types, String keyword, Pageable pageable)
      {
        QCommunity community = QCommunity.community;

        JPQLQuery<Community> communityJPQLQuery = from(community);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if ((types != null && types.length > 0 && types[0].equals("n")) && keyword != null)
          {
                booleanBuilder.or(community.communityName.contains(keyword));

//            for (String type : types)
//              {
//                switch (type)
//                {
//                  case "n":
//                    booleanBuilder.or(community.communityName.contains(keyword));
//                    break;
//                }
//
//              }
          }

        communityJPQLQuery.where(booleanBuilder);

        communityJPQLQuery.distinct();
        communityJPQLQuery.groupBy(community);
        getQuerydsl().applyPagination(pageable, communityJPQLQuery);
        JPQLQuery<Community> selectQuery = communityJPQLQuery.select(community);
        List<Community> communityList = selectQuery.fetch();

        List<CommunityDTO> dtoList = communityList.stream().map(community1 ->
          {
            CommunityDTO dto = CommunityDTO.builder()
                .communityId(community1.getCommunityId())
                .communityName(community1.getCommunityName())
                .build();
            return dto;
          }).toList();

        long totalCount = selectQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
      }
  }
