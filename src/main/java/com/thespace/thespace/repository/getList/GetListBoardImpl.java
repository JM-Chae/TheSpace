package com.thespace.thespace.repository.getList;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.QBoard;
import com.thespace.thespace.domain.QReply;
import com.thespace.thespace.dto.BoardDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Slf4j
public class GetListBoardImpl extends QuerydslRepositorySupport implements GetListBoard
  {
    public GetListBoardImpl()
      {
        super(Board.class);
      }

    public Page<BoardDTO> getList(String[] types, String keyword, Pageable pageable)
    {
      QBoard board = QBoard.board;
      QReply reply = QReply.reply;

      JPQLQuery<Board> boardJPQLQuery = from(board);
      if ((types != null && types.length > 0) && keyword != null)
        {
          BooleanBuilder booleanBuilder = new BooleanBuilder();
          for (String type : types)
            {
              switch (type)
                {
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
          boardJPQLQuery.where(booleanBuilder);
        }
      boardJPQLQuery.distinct();
      boardJPQLQuery.groupBy(board);
      getQuerydsl().applyPagination(pageable, boardJPQLQuery);
      JPQLQuery<Board> selectQuery = boardJPQLQuery.select(board);
      List<Board> boardList = selectQuery.fetch();

      List<BoardDTO> dtoList = boardList.stream().map(board1 ->
        {
          BoardDTO dto = BoardDTO.builder()
              .bno(board1.getBno())
              .title(board1.getTitle())
              .content(board1.getContent())
              .writer(board1.getWriter())
              .categoryName(board1.getCategory().getCategoryName())
              .path(board1.getPath())
              .createDate(board1.getCreateDate())
              .modDate(board1.getModDate())
              .build();

          return dto;
        }).toList();

      long totalCount = selectQuery.fetchCount();

      return new PageImpl<>(dtoList, pageable, totalCount);
    }
  }
