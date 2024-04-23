package com.nawabali.nawabali.controller;

import com.nawabali.nawabali.constant.Category;
import com.nawabali.nawabali.constant.Period;
import com.nawabali.nawabali.domain.elasticsearch.PostSearch;
import com.nawabali.nawabali.dto.PostDto;
import com.nawabali.nawabali.security.UserDetailsImpl;
import com.nawabali.nawabali.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "게시물 API", description = "게시물 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Slf4j
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시물 생성" ,
            description =
                    """
                    - multipart/form-data 형식의 게시물 생성
                    - 이미지 파일 최대 5장
                    """)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PostDto.ResponseDto> createPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestPart("requestDto") PostDto.RequestDto requestDto,
            @RequestPart("files") List<MultipartFile> files) {
        PostDto.ResponseDto responseDto = postService.createPost(userDetails.getUser(), requestDto, files);
        return ResponseEntity.ok(responseDto);
    }


    @Operation(
            summary = "최신 게시물 조회",
            description = "생성일 기준으로 최신 게시물을 조회합니다. 페이징 파라미터를 사용하여 결과를 페이지별로 나눌 수 있습니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
                    @Parameter(name = "size", description = "페이지 당 게시물 수", example = "10"),
                    @Parameter(name = "sort", description = "정렬 기준과 방향, 예: createdAt,desc(생성일 내림차순 정렬)", example = "createdAt,desc")
            }
    )
    @GetMapping
    public ResponseEntity<Slice<PostDto.ResponseDto>> getPostsByLatest(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<PostDto.ResponseDto> postsSlice = postService.getPostsByLatest(pageable);
        return ResponseEntity.ok(postsSlice);
    }

    @Operation(summary = "게시물 카테고리 or 구 로 조회",
            description = "category 또는 district 를 이용한 게시물 조회 , 둘중 하나가 null이어도 상관없다.",
            parameters = {
                    @Parameter(name = "category", description = "FOOD,PHOTOZONE,CAFE 3가지의 카테고리를 입력", example = "FOOD"),
                    @Parameter(name = "district", description = "해당하는 구를 입력", example = "gangnam")
            })
    @GetMapping("/filtered")
    public ResponseEntity<Slice<PostDto.ResponseDto>> getPostByFiltered(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String district,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {

        Slice<PostDto.ResponseDto> posts = postService.getPostByCategory(category, district, pageable);
        return ResponseEntity.ok(posts);
    }


    @Operation(summary = "게시물 상세 조회",
            description = "postId 를 이용한 게시물 상세 조회, 댓글 조회 api는 따로 구현",
            parameters = {
                    @Parameter(name = "postId", description = "postId 로 게시물을 검색", example = "postId : 1")
            })
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto.ResponseDetailDto> getPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostDto.ResponseDetailDto responseDto = postService.getPost(postId, userDetails);
        return ResponseEntity.ok(responseDto);
    }


    @Operation(summary = "일주일 동안 좋아요 기준 상위 10개 게시물 조회",
               description =
                       """
                       category 또는 district 또는 기간(일주일,한달) 를 이용한 좋아요 기준 상위 10개 게시물을 조회
                       * period 는 WEEK 와 MONTH 로 구분합니다.
                       """)
    @GetMapping("/top-like")
    public ResponseEntity<List<PostDto.ResponseDto>> getPostByLike(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Period period) {
        List<PostDto.ResponseDto> responseDto = postService.getPostByLike(category, district, period);
        return ResponseEntity.ok(responseDto);
    }


    @Operation(summary = "카테고리 별 일주일(한달) 동안 게시글이 가장 많았던 구와 게시글 수 출력",
            description =
                    """
                    category 또는 period 를 이용한 게시글이 제일 많았던 구 및 게시글 개수를 조회합니다.
                    """)
    @GetMapping("/top-district")
    public ResponseEntity<PostDto.SortDistrictDto> getDistrictByCategory(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Period period) {
        PostDto.SortDistrictDto result = postService.getDistrictByCategory(category, period);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "구 와 한달기준 각 카테고리의 게시글 개수를 출력",
            description =
                    """
                    district 로 한달간 각 카테고리의 게시글 수를 출력합니다.
                    """)
    @GetMapping("/sort-category")
    public ResponseEntity<List<PostDto.SortCategoryDto>> getCategoryByPost(
            @RequestParam(required = false) String district
    ) {
        List<PostDto.SortCategoryDto> result = postService.getCategoryByPost(district);
        return ResponseEntity.ok(result);
    }




    @Operation(summary = "게시물 수정", description = "postId 를 이용한 게시물 수정")
    @PatchMapping("/{postId}")
    public ResponseEntity<PostDto.PatchDto> updatePost(@PathVariable Long postId,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @RequestBody PostDto.PatchDto patchDto) {
        PostDto.PatchDto responseDto = postService.updatePost(postId,userDetails.getUser(),patchDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "게시물 삭제", description = "postId 를 이용한 게시물 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDto.DeleteDto> deletePost(@PathVariable Long postId) {
        PostDto.DeleteDto deleteDto = postService.deletePost(postId);
        return ResponseEntity.ok(deleteDto);
    }

    @Operation(summary = "게시물 내용 기반 검색", description = "게시물의 내용(contents)으로 검색이 가능합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<PostSearch>> searchPost(@RequestParam("query") String contents) {
        List<PostSearch> postDslDto = postService.searchByContents(contents);
        return ResponseEntity.ok(postDslDto);
    }

    @Operation(summary = "동네별 점수 전체 조회", description = "동네(구)를 넣으면 총 게시물 수 / 좋아요 수 / 동네인증 수 조회 가능합니다")
    @GetMapping("/district")
    public ResponseEntity<List<PostDto.DistrictDto>> districtMap() {
        List<PostDto.DistrictDto> districtDtoList = postService.districtMap();
        return ResponseEntity.ok(districtDtoList);
    }
}

