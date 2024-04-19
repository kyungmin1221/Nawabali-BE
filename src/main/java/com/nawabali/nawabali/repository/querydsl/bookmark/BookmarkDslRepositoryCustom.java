package com.nawabali.nawabali.repository.querydsl.bookmark;

import com.nawabali.nawabali.domain.User;
import com.nawabali.nawabali.dto.BookMarkDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface BookmarkDslRepositoryCustom {

    Slice<BookMarkDto.UserBookmarkDto> getUserBookmarks(User user, Pageable pageable);
}
