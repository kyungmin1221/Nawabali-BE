package com.nawabali.nawabali.domain.elasticsearch;

import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.UserDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "users")
public class UserSearch {
    @Id
    private String id;
    @Field(type = FieldType.Text)
    private String nickname;
    @Field(type=FieldType.Text)
    private String imgUrl;


    public UserSearch(User user, String imgUrl) {
        this.id = user.getId().toString();
        this.nickname = user.getNickname();
        this.imgUrl = imgUrl;
    }
}
