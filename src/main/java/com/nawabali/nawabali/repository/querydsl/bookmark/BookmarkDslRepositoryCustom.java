package com.nawabali.nawabali.repository.querydsl.bookmark;

import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.BookMarkDto;

import java.util.List;

public interface BookmarkDslRepositoryCustom {

    public List<BookMarkDto.UserBookmarkDto> getUserBookmarks(User user);
}
