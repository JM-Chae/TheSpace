package com.thespace.spaceweb.reply;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReply is a Querydsl query type for Reply
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReply extends EntityPathBase<Reply> {

    private static final long serialVersionUID = 242737874L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReply reply = new QReply("reply");

    public final com.thespace.common.QBaseEntity _super = new com.thespace.common.QBaseEntity(this);

    public final com.thespace.spaceweb.board.QBoard board;

    public final NumberPath<Long> childCount = createNumber("childCount", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final NumberPath<Long> parentRno = createNumber("parentRno", Long.class);

    public final StringPath replyContent = createString("replyContent");

    public final NumberPath<Long> rno = createNumber("rno", Long.class);

    public final StringPath tag = createString("tag");

    public final NumberPath<Long> taggedCount = createNumber("taggedCount", Long.class);

    public final NumberPath<Long> tagRno = createNumber("tagRno", Long.class);

    public final com.thespace.spaceweb.user.QUser user;

    public final NumberPath<Long> vote = createNumber("vote", Long.class);

    public QReply(String variable) {
        this(Reply.class, forVariable(variable), INITS);
    }

    public QReply(Path<? extends Reply> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReply(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReply(PathMetadata metadata, PathInits inits) {
        this(Reply.class, metadata, inits);
    }

    public QReply(Class<? extends Reply> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new com.thespace.spaceweb.board.QBoard(forProperty("board"), inits.get("board")) : null;
        this.user = inits.isInitialized("user") ? new com.thespace.spaceweb.user.QUser(forProperty("user")) : null;
    }

}

