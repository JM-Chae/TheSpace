package com.thespace.spaceweb.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard extends EntityPathBase<Board> {

    private static final long serialVersionUID = -1299206710L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoard board = new QBoard("board");

    public final com.thespace.common.QBaseEntity _super = new com.thespace.common.QBaseEntity(this);

    public final NumberPath<Long> bno = createNumber("bno", Long.class);

    public final com.thespace.spaceweb.category.QCategory category;

    public final com.thespace.spaceweb.community.QCommunity community;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final SetPath<BoardFile, QBoardFile> fileSet = this.<BoardFile, QBoardFile>createSet("fileSet", BoardFile.class, QBoardFile.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final NumberPath<Long> rCount = createNumber("rCount", Long.class);

    public final ListPath<com.thespace.spaceweb.reply.Reply, com.thespace.spaceweb.reply.QReply> reply = this.<com.thespace.spaceweb.reply.Reply, com.thespace.spaceweb.reply.QReply>createList("reply", com.thespace.spaceweb.reply.Reply.class, com.thespace.spaceweb.reply.QReply.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final com.thespace.spaceweb.user.QUser user;

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public final NumberPath<Long> vote = createNumber("vote", Long.class);

    public QBoard(String variable) {
        this(Board.class, forVariable(variable), INITS);
    }

    public QBoard(Path<? extends Board> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoard(PathMetadata metadata, PathInits inits) {
        this(Board.class, metadata, inits);
    }

    public QBoard(Class<? extends Board> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.thespace.spaceweb.category.QCategory(forProperty("category"), inits.get("category")) : null;
        this.community = inits.isInitialized("community") ? new com.thespace.spaceweb.community.QCommunity(forProperty("community")) : null;
        this.user = inits.isInitialized("user") ? new com.thespace.spaceweb.user.QUser(forProperty("user")) : null;
    }

}

