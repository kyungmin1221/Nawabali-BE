package com.nawabali.nawabali.dto;

import lombok.*;

public class UserDto {
    @Getter
    @Setter
    public static class LoginRequestDto{
        String email;
        String password;
    }
}
