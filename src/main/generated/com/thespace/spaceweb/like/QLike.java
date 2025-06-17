package com.thespace.spaceweb.like;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLike is a Querydsl query type for Like
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLike extends EntityPathBase<Like> {

    private static final long serialVersionUID = -1544782150L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLike like = new QLike("like1");

    public final com.thespace.spaceweb.board.QBoard board;

    public final NumberPath<Long> Lno = createNumber("Lno", Long.class);

    public final com.thespace.spaceweb.reply.QReply reply;

    public final com.thespace.spaceweb.user.QUser user;

    public QLike(String variable) {
        this(Like.class, forVariable(variable), INITS);
    }

    public QLike(Path<? extends Like> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLike(PathMetadata metadata, PathInits inits) {
        this(Like.class, metadata, inits);
    }

    public QLike(Class<? extends Like> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new com.thespace.spaceweb.board.QBoard(forProperty("board"), inits.get("board")) : null;
        this.reply = inits.isInitialized("reply") ? new com.thespace.spaceweb.reply.QReply(forProperty("reply"), inits.get("reply")) : null;
        this.user = inits.isInitialized("user") ? new com.thespace.spaceweb.user.QUser(forProperty("user")) : null;
    }

}

