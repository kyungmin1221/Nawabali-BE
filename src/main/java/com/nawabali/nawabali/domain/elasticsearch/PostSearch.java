package com.nawabali.nawabali.domain.elasticsearch;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "post")
@Getter @Setter
public class PostSearch {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String contents;

    @Field(type = FieldType.Long)
    private Long postId;

}
