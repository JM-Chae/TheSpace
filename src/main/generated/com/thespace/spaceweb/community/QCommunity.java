package com.thespace.spaceweb.community;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommunity is a Querydsl query type for Community
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommunity extends EntityPathBase<Community> {

    private static final long serialVersionUID = 1345785872L;

    public static final QCommunity community = new QCommunity("community");

    public final com.thespace.common.QBaseEntity _super = new com.thespace.common.QBaseEntity(this);

    public final ListPath<com.thespace.spaceweb.category.Category, com.thespace.spaceweb.category.QCategory> category = this.<com.thespace.spaceweb.category.Category, com.thespace.spaceweb.category.QCategory>createList("category", com.thespace.spaceweb.category.Category.class, com.thespace.spaceweb.category.QCategory.class, PathInits.DIRECT2);

    public final NumberPath<Long> communityId = createNumber("communityId", Long.class);

    public final StringPath communityName = createString("communityName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final StringPath description = createString("description");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public QCommunity(String variable) {
        super(Community.class, forVariable(variable));
    }

    public QCommunity(Path<? extends Community> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommunity(PathMetadata metadata) {
        super(Community.class, metadata);
    }

}

