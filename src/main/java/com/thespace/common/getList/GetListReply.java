package com.thespace.common.getList;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thespace.spaceweb.reply.QReply;
import com.thespace.spaceweb.reply.Reply;
import com.thespace.spaceweb.reply.ReplyDTOs.Info;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetListReply {

    private final JPAQueryFactory queryFactory;

    public Page<Info> getListReply(Long bno, Pageable pageable) {
        QReply reply = QReply.reply;

        JPAQuery<Reply> query = queryFactory
            .selectFrom(reply)
            .where(reply.board.bno.eq(bno))
            .distinct();

        List<Reply> replyList = QuerydslUtils.applyPagination(query, pageable, reply)
            .fetch();

        long totalCount = Optional.ofNullable(queryFactory
            .select(reply.countDistinct())
            .from(reply)
            .where(reply.board.bno.eq(bno))
            .fetchOne())
            .orElse(0L);

        return returnList(replyList, pageable, totalCount);
    }

    public Page<Info> returnList(List<Reply> replyList, Pageable pageable, long totalCount) {

        List<Info> rdtoList = replyList.stream().map(reply1 ->
            Info.builder()
                .rno(reply1.getRno())
                .replyWriterUuid(reply1.getUser().getUuid())
                .replyContent(reply1.getReplyContent())
                .replyWriter(reply1.getUser().getName())
                .childCount(reply1.getChildCount())
                .taggedCount(reply1.getTaggedCount())
                .tag(reply1.getTag())
                .replyDate(reply1.getCreateDate())
                .parentRno(reply1.getParentRno())
                .tagRno(reply1.getTagRno())
                .vote(reply1.getVote())
                .build()).toList();

        return new PageImpl<>(rdtoList, pageable, totalCount);
    }
}
