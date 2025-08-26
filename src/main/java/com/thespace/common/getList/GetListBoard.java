package com.thespace.common.getList;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thespace.common.exception.Exceptions.CommunityNotFound;
import com.thespace.spaceweb.board.Board;
import com.thespace.spaceweb.board.BoardDTOs.Info;
import com.thespace.spaceweb.board.QBoard;
import com.thespace.spaceweb.category.Category;
import com.thespace.spaceweb.category.CategoryRepository;
import com.thespace.spaceweb.community.Community;
import com.thespace.spaceweb.community.CommunityRepository;
import com.thespace.spaceweb.reply.QReply;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetListBoard {

    private final CategoryRepository categoryRepository;
    private final CommunityRepository communityRepository;
    private final JPAQueryFactory queryFactory;

    public Page<Info> getList(String[] types, String keyword, Pageable pageable, Long communityId,
        Long categoryId) {
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if ((types != null && types.length > 0) && keyword != null) {
            for (String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.user.name.contains(keyword));
                        break;
                    case "u":
                        booleanBuilder.or(board.user.uuid.contains(keyword));
                        break;
                    case "r":
                        booleanBuilder.or(reply.replyContent.contains(keyword));
                        break;
                }
            }
        }

        if (!communityId.equals(0L)) {
            Community community = communityRepository.findById(communityId)
                .orElseThrow(CommunityNotFound::new);
            booleanBuilder.and(board.community.eq(community));

            if (!categoryId.equals(0L)) {
                List<Category> categoryList = categoryRepository.findByCommunity(community);
                Optional<Category> categoryCheck = categoryList.stream()
                    .filter(cate -> cate.getId().equals(categoryId))
                    .findFirst();
                if (categoryCheck.isPresent()) {
                    Category categoryObj = categoryCheck.get();
                    booleanBuilder.and(board.category.eq(categoryObj));
                }
            }
        }

        JPAQuery<Board> query = queryFactory
            .selectFrom(board)
            .where(booleanBuilder)
            .distinct();

        List<Board> boardList = QuerydslUtils.applyPagination(query, pageable, board)
            .fetch();

        long totalCount = Optional.ofNullable(queryFactory
            .select(board.countDistinct())
            .from(board)
            .where(booleanBuilder)
            .fetchOne())
            .orElse(0L);

        List<Info> dtoList = boardList.stream()
            .map(Info::fromEntity)
            .toList();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }
}
