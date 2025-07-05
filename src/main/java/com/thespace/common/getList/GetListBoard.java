package com.thespace.common.getList;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.thespace.common.exception.CommunityNotFound;
import com.thespace.spaceweb.board.Board;
import com.thespace.spaceweb.board.BoardDTOs;
import com.thespace.spaceweb.board.BoardDTOs.Info;
import com.thespace.spaceweb.board.QBoard;
import com.thespace.spaceweb.category.Category;
import com.thespace.spaceweb.category.CategoryDTOs;
import com.thespace.spaceweb.category.CategoryRepository;
import com.thespace.spaceweb.community.Community;
import com.thespace.spaceweb.community.CommunityDTOs;
import com.thespace.spaceweb.community.CommunityRepository;
import com.thespace.spaceweb.reply.QReply;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;

@Component
public class GetListBoard extends QuerydslRepositorySupport {

    private final CategoryRepository categoryRepository;
    private final CommunityRepository communityRepository;

    public GetListBoard(CategoryRepository categoryRepository,
        CommunityRepository communityRepository) {
        super(Board.class);
        this.categoryRepository = categoryRepository;
        this.communityRepository = communityRepository;
    }

    public Page<Info> getList(String[] types, String keyword, Pageable pageable, Long communityId,
        Long categoryId) {
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> boardJPQLQuery = from(board);
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
            Community community = communityRepository.findById(communityId).orElseThrow(CommunityNotFound::new);
            booleanBuilder.and(board.community.eq(community));

            if (!categoryId.equals(0L)) {
                List<Category> categoryList = categoryRepository.findByCommunity(community);
                Optional<Category> categoryCheck = categoryList.stream()
                    .filter(cate -> cate.getId().equals(categoryId)).findFirst();
                if (categoryCheck.isPresent()) {
                    Category categoryObj = categoryCheck.get();
                    booleanBuilder.and(board.category.eq(categoryObj));
                }
            }
        }

        boardJPQLQuery.where(booleanBuilder);

        boardJPQLQuery.distinct();
        boardJPQLQuery.groupBy(board);
        getQuerydsl().applyPagination(pageable, boardJPQLQuery);
        JPQLQuery<Board> selectQuery = boardJPQLQuery.select(board);
        List<Board> boardList = selectQuery.fetch();

        List<Info> dtoList = boardList.stream().map(board1 ->
        {
            Community community1 = board1.getCommunity();
            Category category1 = board1.getCategory();

            return BoardDTOs.Info.builder()
                .bno(board1.getBno())
                .title(board1.getTitle())
                .content(board1.getContent())
                .writer(board1.getUser().getName())
                .writerUuid(board1.getUser().getUuid())
                .communityInfo(new CommunityDTOs.Info(
                    community1.getId(),
                    community1.getName(),
                    community1.getCreateDate(),
                    community1.getModDate(),
                    community1.getDescription()
                ))
                .categoryInfo(new CategoryDTOs.Info(
                    category1.getId(),
                    category1.getName(),
                    category1.getType(),
                    category1.getCreateDate(),
                    category1.getModDate(),
                    category1.getCommunity().getId()
                ))
                .vote(board1.getVote())
                .viewCount(board1.getViewCount())
                .modDate(board1.getModDate())
                .createDate(board1.getCreateDate())
                .rCount(board1.getRCount())
                .fileNames(board1.getFileSet().stream().sorted().map(boardFile ->
                        boardFile.getFileId() + "_" + boardFile.getFileName())
                    .toList())
                .build();
        })
            .toList();

        long totalCount = selectQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }
}
