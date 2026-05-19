package com.example.game_match.imagepost.controller;

import com.example.game_match.global.response.CommonResponse;
import com.example.game_match.imagepost.dto.ImagePostResponseDto;
import com.example.game_match.imagepost.service.ImagePostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImagePostController {
    private final ImagePostService imagePostService;

    // 게시글 ID에 여러 이미지 파일을 업로드하고 저장된 이미지 정보를 반환한다.
    @PostMapping("/posts/{postId}/images")
    public ResponseEntity<CommonResponse<List<ImagePostResponseDto>>> uploadImages(
            @PathVariable Integer postId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                imagePostService.createImagePostByFiles(files, postId)
        ));
    }

    // 게시글 ID 기준으로 업로드된 이미지 URL 목록을 조회한다.
    @GetMapping("/posts/{postId}/images")
    public ResponseEntity<CommonResponse<List<String>>> getImageUrlsByPostId(@PathVariable Integer postId) {
        return ResponseEntity.ok(CommonResponse.success(imagePostService.getImageUrlsByPostId(postId)));
    }
}
