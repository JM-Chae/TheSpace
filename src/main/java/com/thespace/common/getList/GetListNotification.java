package com.thespace.common.getList;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thespace.common.notification.Notification;
import com.thespace.common.notification.NotificationDTOs.DTO;
import com.thespace.common.notification.QNotification;
import com.thespace.spaceweb.user.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetListNotification {

    private final JPAQueryFactory queryFactory;

    public Page<DTO> getList(User user, /*Boolean isRead,*/ Pageable pageable) {
        QNotification notification = QNotification.notification;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(notification.user.eq(user));

//        if (isRead != null) {
//            booleanBuilder.and(notification.is_read.eq(isRead));
//        }

        JPAQuery<Notification> query = queryFactory
            .selectFrom(notification)
            .where(booleanBuilder)
            .distinct();

        List<Notification> notificationList = QuerydslUtils.applyPagination(query, pageable, notification)
        .fetch();

        long totalCount = Optional.ofNullable(queryFactory
            .select(notification.countDistinct())
            .from(notification)
            .where(booleanBuilder)
            .fetchOne())
            .orElse(0L);

        List<DTO> dtoList = notificationList.stream()
            .map(DTO::fromEntity)
            .toList();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }
}
