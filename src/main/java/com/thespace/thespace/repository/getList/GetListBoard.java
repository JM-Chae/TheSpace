package com.thespace.thespace.repository.getList;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.QBoard;
import com.thespace.thespace.domain.QReply;
import com.thespace.thespace.dto.board.BoardDTO;
import com.thespace.thespace.repository.CategoryRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;

@Component
public class GetListBoard extends QuerydslRepositorySupport {

    private final CategoryRepository categoryRepository;

    public GetListBoard(CategoryRepository categoryRepository) {
        super(Board.class);
        this.categoryRepository = categoryRepository;
    }

    public Page<BoardDTO> getList(String[] types, String keyword, Pageable pageable, String path,
        String category) {
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
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                    case "u":
                        booleanBuilder.or(board.writerUuid.contains(keyword));
                        break;
                    case "r":
                        booleanBuilder.or(reply.replyContent.contains(keyword));
                        break;
                }
            }
        }

        if (!path.isEmpty()) {
            booleanBuilder.and(board.path.eq(path));
        }

        if (!Objects.equals(category, "")) {
            List<Category> categoryList = categoryRepository.findByPath(path);
            Optional<Category> categoryCheck = categoryList.stream()
                .filter(cate -> cate.getCategoryName().equals(category)).findFirst();
            if (categoryCheck.isPresent()) {
                Category categoryObj = categoryCheck.get();
                booleanBuilder.and(board.category.eq(categoryObj));
            }
        }

        boardJPQLQuery.where(booleanBuilder);

        boardJPQLQuery.distinct();
        boardJPQLQuery.groupBy(board);
        getQuerydsl().applyPagination(pageable, boardJPQLQuery);
        JPQLQuery<Board> selectQuery = boardJPQLQuery.select(board);
        List<Board> boardList = selectQuery.fetch();

        List<BoardDTO> dtoList = boardList.stream().map(board1 ->
                BoardDTO.builder()
                    .bno(board1.getBno())
                    .title(board1.getTitle())
                    .content(board1.getContent())
                    .path(board1.getPath())
                    .writer(board1.getWriter())
                    .writerUuid(board1.getWriterUuid())
                    .createDate(board1.getCreateDate())
                    .modDate(board1.getModDate())
                    .viewCount(board1.getViewCount())
                    .vote(board1.getVote())
                    .fileNames(board1.getFileSet().stream().sorted().map(boardFile ->
                            boardFile.getFileId() + "_" + boardFile.getFileName())
                        .toList())
                    .categoryId(board1.getCategory().getCategoryId())
                    .rCount(board1.getRCount())
                    .build())
            .toList();

        long totalCount = selectQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }
}
