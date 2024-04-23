package com.nawabali.nawabali.constant;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTown is a Querydsl query type for Town
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QTown extends BeanPath<Town> {

    private static final long serialVersionUID = 266153647L;

    public static final QTown town = new QTown("town");

    public final StringPath district = createString("district");

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath placeAddr = createString("placeAddr");

    public final StringPath placeName = createString("placeName");

    public QTown(String variable) {
        super(Town.class, forVariable(variable));
    }

    public QTown(Path<? extends Town> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTown(PathMetadata metadata) {
        super(Town.class, metadata);
    }

}

