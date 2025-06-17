package com.thespace.common.getList;

import com.querydsl.jpa.JPQLQuery;
import com.thespace.spaceweb.reply.QReply;
import com.thespace.spaceweb.reply.Reply;
import com.thespace.spaceweb.reply.ReplyDTOs.Info;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;

@Component
public class GetListReply extends QuerydslRepositorySupport {

    public GetListReply() {
        super(Reply.class);
    }

    public Page<Info> getListReply(Long bno, Pageable pageable) {
        QReply reply = QReply.reply;
        JPQLQuery<Reply> replyJPQLQuery = from(reply);

        replyJPQLQuery.where(reply.board.bno.eq(bno));
        replyJPQLQuery.where(reply.path.eq(String.valueOf(bno) + '/'));

        replyJPQLQuery.groupBy(reply);

        getQuerydsl().applyPagination(pageable, replyJPQLQuery);

        JPQLQuery<Reply> selectQuery = replyJPQLQuery.select(reply);

        return returnList(selectQuery, pageable);
    }

    public Page<Info> getListNestedReply(Long rno, Long bno, Pageable pageable) {
        QReply reply = QReply.reply;
        JPQLQuery<Reply> replyJPQLQuery = from(reply);

        replyJPQLQuery.where(reply.board.bno.eq(bno));
        replyJPQLQuery.where(reply.path.eq(String.valueOf(bno) + '/' + rno));

        replyJPQLQuery.groupBy(reply);
        getQuerydsl().applyPagination(pageable, replyJPQLQuery);

        JPQLQuery<Reply> selectQuery = replyJPQLQuery.select(reply);

        return returnList(selectQuery, pageable);
    }

    public Page<Info> returnList(JPQLQuery<Reply> selectQuery, Pageable pageable) {

        List<Reply> replyList = selectQuery.fetch();

        List<Info> rdtoList = replyList.stream().map(reply1 ->
            Info.builder()
                .rno(reply1.getRno())
                .replyWriterUuid(reply1.getUser().getUuid())
                .replyContent(reply1.getReplyContent())
                .replyWriter(reply1.getUser().getName())
                .path(reply1.getPath())
                .tag(reply1.getTag())
                .replyDate(reply1.getCreateDate())
                .isNested(reply1.getIsNested())
                .vote(reply1.getVote())
                .build()).toList();

        long totalCount = selectQuery.fetchCount();

        return new PageImpl<>(rdtoList, pageable, totalCount);
    }
}
