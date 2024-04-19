package com.nawabali.nawabali.constant;

import lombok.Getter;

@Getter
public enum UserRankEnum {
    RESIDENT("주민",5L,0L),
    NATIVE_PERSON("토박이",10L,100L),
    LOCAL_ELDER("터줏대감",0L,0L);

    private final String name;
    private final Long needPosts;
    private final Long needLikes;

    UserRankEnum(String name, Long needPosts, Long needTotalLikes) {
        this.name = name;
        this.needPosts = needPosts;
        this.needLikes = needTotalLikes;
    }
}
