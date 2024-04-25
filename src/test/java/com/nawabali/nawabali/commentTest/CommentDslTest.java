//package com.nawabali.nawabali.commentTest;
//
//import com.nawabali.nawabali.constant.*;
//import com.nawabali.nawabali.domain.BookMark;
//import com.nawabali.nawabali.domain.Post;
//import com.nawabali.nawabali.domain.User;
//import com.nawabali.nawabali.dto.BookMarkDto;
//import com.nawabali.nawabali.exception.CustomException;
//import com.nawabali.nawabali.exception.ErrorCode;
//import com.nawabali.nawabali.repository.BookMarkRepository;
//import com.nawabali.nawabali.repository.PostRepository;
//import com.nawabali.nawabali.repository.UserRepository;
//import com.nawabali.nawabali.service.BookMarkService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.StopWatch;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@SpringBootTest
//public class CommentDslTest {
//
//    @Autowired
//    private BookMarkService bookMarkService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private BookMarkRepository bookMarkRepository;
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @BeforeEach
//    public void setup() {
//        User testUser = User.builder()
//                .nickname("testUser")
//                .email("test@example.com")
//                .password("password")
//                .role(UserRoleEnum.USER)
//                .address(new Address("서울시", "강남구")) // 가정: Address 클래스에 @Embeddable 어노테이션이 적용되어 있어야 함
//                .rank(UserRankEnum.LOCAL_ELDER)
//                .build();
//        userRepository.save(testUser);
//
//        // Post 엔티티 생성 및 초기화
//        Post testPost = Post.builder()
//                .contents("This is a test post")
//                .createdAt(LocalDateTime.now())
//                .modifiedAt(LocalDateTime.now())
//                .category(Category.CAFE)
//                .town(new Town(10.11, 1.11,"성북구"))
//                .user(testUser)
//                .build();
//        postRepository.save(testPost);
//
//        int count = 100_000;
//
//        for (int i = 0; i < count; i++) {
//            BookMark bookMark = BookMark.builder()
//                    .user(testUser)
//                    .post(testPost)
//                    .status(true)
//                    .build();
//            bookMarkRepository.save(bookMark);
//        }
//    }
//
//
//    //  37 ms - jpa 사용
////    @Test
////    public void benchmarkGetBookmarks() {
////        // 테스트를 위한 사용자 조회
////        User user = userRepository.findById(1L).orElseThrow();
////
////        StopWatch stopWatch = new StopWatch();
////        stopWatch.start();
////
////        List<BookMarkDto.UserBookmarkDto> bookmarksJpaMethod = bookMarkService.getBookmarks1(user);
////        for (BookMarkDto.UserBookmarkDto userBookmarkDto : bookmarksJpaMethod) {
////            System.out.println("userBookmarkId() = " + userBookmarkDto.getBookmarkId());
////        }
////
////        stopWatch.stop();
////        System.out.println("Jpa 실행시간 측정 :  " + stopWatch.getTotalTimeSeconds());
////        System.out.println(stopWatch.prettyPrint());
////
////    }
//
//    // 0.55 ms - query dsl 사용
////    @Test
////    public void benchmarkGetBookmarks2() {
////        // 테스트를 위한 사용자 조회
////        User user = userRepository.findById(1L).orElseThrow();
////
////        StopWatch stopWatch = new StopWatch();
////        stopWatch.start();
////
////        List<BookMarkDto.UserBookmarkDto> bookmarksQuerydslMethod = bookMarkService.getBookmarks(user);
////        for (BookMarkDto.UserBookmarkDto userBookmarkDto : bookmarksQuerydslMethod) {
////            System.out.println("userBookmarkId() = " + userBookmarkDto.getBookmarkId());
////        }
////
////        stopWatch.stop();
////        System.out.println("QueryDSL 실행시간 :  " + stopWatch.getTotalTimeSeconds());
////        System.out.println(stopWatch.prettyPrint());
////    }
//
//}
