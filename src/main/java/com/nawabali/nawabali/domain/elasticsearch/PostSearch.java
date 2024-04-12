package com.nawabali.nawabali.domain.elasticsearch;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

@AllArgsConstructor
@NoArgsConstructor
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
