package com.thespace.thespace.repository.getList;

import com.querydsl.jpa.JPQLQuery;
import com.thespace.thespace.domain.QReply;
import com.thespace.thespace.domain.Reply;
import com.thespace.thespace.dto.ReplyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class GetListReplyImpl extends QuerydslRepositorySupport implements GetListReply
  {
    public GetListReplyImpl()
      {
        super(Reply.class);
      }

    public Page<ReplyDTO> getListReply(Long bno, String[] types, String keyword, Pageable pageable)
      {
        QReply reply = QReply.reply;
        JPQLQuery<Reply> replyJPQLQuery = from(reply);

        replyJPQLQuery.where(reply.board.bno.eq(bno));
        replyJPQLQuery.where(reply.path.eq(String.valueOf(bno)+'/'));

        replyJPQLQuery.groupBy(reply);

        getQuerydsl().applyPagination(pageable, replyJPQLQuery);

        JPQLQuery<Reply> selectQuery = replyJPQLQuery.select(reply);
        List<Reply> replyList = selectQuery.fetch();

        List<ReplyDTO> rdtoList = replyList.stream().map(reply1 ->
          {
            ReplyDTO rdto = ReplyDTO.builder()
                .rno(reply1.getRno())
                .bno(reply1.getBoard().getBno())
                .replyWriterUuid(reply1.getReplyWriterUuid())
                .replyContent(reply1.getReplyContent())
                .replyWriter(reply1.getReplyWriter())
                .path(reply1.getPath())
                .tag(reply1.getTag())
                .replyDate(reply1.getCreateDate())
                .isNested(reply1.getIsNested())
                .build();

            return rdto;
          }).toList();

        long totalCount = selectQuery.fetchCount();

        return new PageImpl<>(rdtoList, pageable, totalCount);
      }

    public Page<ReplyDTO> getListNestedReply(Long rno, Long bno, String[] types, String keyword, Pageable pageable)
      {
        QReply reply = QReply.reply;
        JPQLQuery<Reply> replyJPQLQuery = from(reply);

        replyJPQLQuery.where(reply.board.bno.eq(bno));
        replyJPQLQuery.where(reply.path.eq(String.valueOf(bno)+'/'+String.valueOf(rno)));

        replyJPQLQuery.groupBy(reply);

        getQuerydsl().applyPagination(pageable, replyJPQLQuery);

        JPQLQuery<Reply> selectQuery = replyJPQLQuery.select(reply);
        List<Reply> replyList = selectQuery.fetch();

        List<ReplyDTO> rdtoList = replyList.stream().map(reply1 ->
          {
            ReplyDTO rdto = ReplyDTO.builder()
                .rno(reply1.getRno())
                .bno(reply1.getBoard().getBno())
                .replyWriterUuid(reply1.getReplyWriterUuid())
                .replyContent(reply1.getReplyContent())
                .replyWriter(reply1.getReplyWriter())
                .path(reply1.getPath())
                .tag(reply1.getTag())
                .replyDate(reply1.getCreateDate())
                .isNested(reply1.getIsNested())
                .build();

            return rdto;
          }).toList();

        long totalCount = selectQuery.fetchCount();

        return new PageImpl<>(rdtoList, pageable, totalCount);
      }
  }
