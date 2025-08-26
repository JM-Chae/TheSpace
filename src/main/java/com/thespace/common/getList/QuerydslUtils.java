package com.thespace.common.getList;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class QuerydslUtils {

    public static <T> JPAQuery<T> applyPagination(JPAQuery<T> query, Pageable pageable, EntityPathBase<T> qEntity) {
        for (Sort.Order order : pageable.getSort()) {
            PathBuilder<T> pathBuilder = new PathBuilder<>(qEntity.getType(), qEntity.getMetadata());
            query.orderBy(new OrderSpecifier<>(
                order.isAscending() ? Order.ASC : Order.DESC,
                pathBuilder.getComparable(order.getProperty(), Comparable.class)
            ));
        }

        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        return query;
    }
}