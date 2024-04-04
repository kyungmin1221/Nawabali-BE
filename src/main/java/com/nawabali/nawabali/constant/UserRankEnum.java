package com.nawabali.nawabali.constant;

import lombok.Getter;

@Getter
public enum UserRankEnum {
    RESIDENT("RESIDENT"),
    NATIVE_PERSON("NATIVE_PERSON"),
    LOCAL_ELDER("LOCAL_ELDER");

    UserRankEnum(String rank) {}
}
