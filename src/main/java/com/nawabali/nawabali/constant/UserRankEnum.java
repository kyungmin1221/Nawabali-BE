package com.nawabali.nawabali.constant;

import lombok.Getter;

@Getter
public enum UserRankEnum {
    RESIDENT("주민",1L,1L),
    NATIVE_PERSON("토박이",2L,2L),
    LOCAL_ELDER("터줏대감",3L,3L);

    private final String name;
    private final Long needPosts;
    private final Long needLikes;

    UserRankEnum(String name, Long needPosts, Long needTotalLocalLikes) {
        this.name = name;
        this.needPosts = needPosts;
        this.needLikes = needTotalLocalLikes;
    }
}
