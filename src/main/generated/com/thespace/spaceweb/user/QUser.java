package com.thespace.spaceweb.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1041367238L;

    public static final QUser user = new QUser("user");

    public final com.thespace.common.QBaseEntity _super = new com.thespace.common.QBaseEntity(this);

    public final ListPath<com.thespace.spaceweb.board.Board, com.thespace.spaceweb.board.QBoard> board = this.<com.thespace.spaceweb.board.Board, com.thespace.spaceweb.board.QBoard>createList("board", com.thespace.spaceweb.board.Board.class, com.thespace.spaceweb.board.QBoard.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final StringPath email = createString("email");

    public final StringPath id = createString("id");

    public final StringPath introduce = createString("introduce");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final ListPath<UserRole, QUserRole> roles = this.<UserRole, QUserRole>createList("roles", UserRole.class, QUserRole.class, PathInits.DIRECT2);

    public final SetPath<com.thespace.spacechat.room.Room, com.thespace.spacechat.room.QRoom> rooms = this.<com.thespace.spacechat.room.Room, com.thespace.spacechat.room.QRoom>createSet("rooms", com.thespace.spacechat.room.Room.class, com.thespace.spacechat.room.QRoom.class, PathInits.DIRECT2);

    public final StringPath signature = createString("signature");

    public final StringPath uuid = createString("uuid");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

