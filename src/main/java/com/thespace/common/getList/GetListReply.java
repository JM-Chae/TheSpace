package com.thespace.common.getList;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thespace.spaceweb.reply.QReply;
import com.thespace.spaceweb.reply.Reply;
import com.thespace.spaceweb.reply.ReplyDTOs.Info;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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

        // Only top level replies (parentRno = 0).
        JPAQuery<Reply> parentQuery = queryFactory
            .selectFrom(reply)
            .where(reply.board.bno.eq(bno).and(reply.parentRno.eq(0L)));

        List<Reply> parentReplies = QuerydslUtils.applyPagination(parentQuery, pageable, reply)
            .fetch();

        long totalCount = Optional.ofNullable(queryFactory
            .select(reply.count())
            .from(reply)
            .where(reply.board.bno.eq(bno).and(reply.parentRno.eq(0L)))
            .fetchOne())
            .orElse(0L);

        // Searching children replies with searched top-revel replies.
        List<Long> parentRnos = parentReplies.stream()
            .map(Reply::getRno)
            .toList();

        List<Reply> childReplies = List.of();
        if (!parentRnos.isEmpty()) {
            childReplies = queryFactory
                .selectFrom(reply)
                .where(reply.parentRno.in(parentRnos))
                .orderBy(reply.createDate.asc())
                .fetch();
        }

        List<Info> resultDTOs = mapToDTOs(parentReplies, childReplies);

        return new PageImpl<>(resultDTOs, pageable, totalCount);
    }

    // List of children to grouping by parents no.
    private List<Info> mapToDTOs(List<Reply> parents, List<Reply> children) {
        Map<Long, List<Info>> childrenDtoMap = children.stream()
            .map(this::convertToInfoDTO)
            .collect(Collectors.groupingBy(Info::parentRno));

        return parents.stream()
            .map(parent -> {
                List<Info> childDTOs = childrenDtoMap.getOrDefault(parent.getRno(), List.of());
                return Info.builder()
                    .rno(parent.getRno())
                    .replyContent(parent.getReplyContent())
                    .replyWriter(parent.getUser().getName())
                    .replyWriterUuid(parent.getUser().getUuid())
                    .tag(parent.getTag())
                    .replyDate(parent.getCreateDate())
                    .childCount(parent.getChildCount())
                    .taggedCount(parent.getTaggedCount())
                    .parentRno(parent.getParentRno())
                    .tagRno(parent.getTagRno())
                    .vote(parent.getVote())
                    .children(childDTOs)
                    .build();
            })
            .toList();
    }

    // A child reply cannot have its own children, so an empty list is assigned to its children field.
    private Info convertToInfoDTO(Reply reply) {
        return Info.builder()
            .rno(reply.getRno())
            .replyWriterUuid(reply.getUser().getUuid())
            .replyContent(reply.getReplyContent())
            .replyWriter(reply.getUser().getName())
            .childCount(reply.getChildCount())
            .taggedCount(reply.getTaggedCount())
            .tag(reply.getTag())
            .replyDate(reply.getCreateDate())
            .parentRno(reply.getParentRno())
            .tagRno(reply.getTagRno())
            .vote(reply.getVote())
            .children(List.of()) // 자식의 자식은 표현하지 않으므로 빈 리스트로 설정
            .build();
    }
}
