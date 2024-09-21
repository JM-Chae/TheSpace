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

    public Page<ReplyDTO> getListReply(String[] types, String keyword, Pageable pageable)
      {
        QReply reply = QReply.reply;
        JPQLQuery<Reply> replyJPQLQuery = from(reply);

        replyJPQLQuery.groupBy(reply);

        getQuerydsl().applyPagination(pageable, replyJPQLQuery);

        JPQLQuery<Reply> selectQuery = replyJPQLQuery.select(reply);
        List<Reply> replyList = selectQuery.fetch();

        List<ReplyDTO> rdtoList = replyList.stream().map(reply1 ->
          {
            ReplyDTO rdto = ReplyDTO.builder()
                .rno(reply1.getRno())
                .replyContent(reply1.getReplyContent())
                .replyWriter(reply1.getReplyWriter())
                .path(reply1.getPath())
                .replyDate(reply1.getCreateDate())
                .build();

            return rdto;
          }).toList();

        long totalCount = selectQuery.fetchCount();

        return new PageImpl<>(rdtoList, pageable, totalCount);
      }
  }
