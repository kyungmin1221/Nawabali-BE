package com.nawabali.nawabali.domain;

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

    private static final long serialVersionUID = 928371592L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final com.nawabali.nawabali.constant.QAddress address;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> kakaoId = createNumber("kakaoId", Long.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final com.nawabali.nawabali.domain.image.QProfileImage profileImage;

    public final EnumPath<com.nawabali.nawabali.constant.UserRankEnum> rank = createEnum("rank", com.nawabali.nawabali.constant.UserRankEnum.class);

    public final EnumPath<com.nawabali.nawabali.constant.UserRoleEnum> role = createEnum("role", com.nawabali.nawabali.constant.UserRoleEnum.class);

    public final StringPath username = createString("username");

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new com.nawabali.nawabali.constant.QAddress(forProperty("address")) : null;
        this.profileImage = inits.isInitialized("profileImage") ? new com.nawabali.nawabali.domain.image.QProfileImage(forProperty("profileImage"), inits.get("profileImage")) : null;
    }

}

