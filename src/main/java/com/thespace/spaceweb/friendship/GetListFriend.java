package com.thespace.spaceweb.friendship;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thespace.common.QuerydslUtils;
import com.thespace.spaceweb.friendship.FriendshipDTOs.Friend;
import com.thespace.spaceweb.user.User;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetListFriend {

    private final JPAQueryFactory queryFactory;

    public Page<Friend> getListFriend(User user, Pageable pageable, String keyword) {
        QFriendship friendship = QFriendship.friendship;



        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(friendship.from.eq(user));
        booleanBuilder.and(friendship.status.eq(Status.ACCEPTED));

        if (!Objects.equals(keyword, "")) {
            booleanBuilder.and(friendship.to.name.contains(keyword));
        }

        JPAQuery<Friendship> query = queryFactory
            .selectFrom(friendship)
            .where(booleanBuilder);

        List<Friendship> friendsList = QuerydslUtils.applyPagination(query, pageable, friendship)
            .fetch();

        long totalCount = Optional.ofNullable(queryFactory
                .select(friendship.count())
                .from(friendship)
                .where(booleanBuilder)
                .fetchOne())
            .orElse(0L);

        List<Friend> resultDTOs = friendsList.stream()
            .map(this::mapToFriend)
            .collect(Collectors.toList());

        return new PageImpl<>(resultDTOs, pageable, totalCount);
    }

    Friend mapToFriend(Friendship friendship) {
        return new Friend(friendship.getFid(),
            friendship.getTo().getSignature(),
            friendship.getTo().getName(),
            friendship.getTo().getUuid(),
            friendship.getTo().getIntroduce(),
            friendship.getNote(),
            friendship.getAcceptedAt()
        );
    }
}