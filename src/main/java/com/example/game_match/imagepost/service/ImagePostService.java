package com.example.game_match.imagepost.service;

import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.imagepost.domain.ImagePost;
import com.example.game_match.imagepost.domain.vo.ImageUrlVo;
import com.example.game_match.imagepost.dto.ImagePostResponseDto;
import com.example.game_match.imagepost.repository.ImagePostRepository;
import com.example.game_match.post.domain.Post;
import com.example.game_match.post.repository.PostRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImagePostService {
    private final ImagePostRepository imagePostRepository;
    private final PostRepository postRepository;

    @Value("${server.url}")
    private String serverUrl;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 게시글에 첨부된 여러 이미지 파일을 저장하고 DB 메타데이터를 저장한다.
    @Transactional
    public List<ImagePostResponseDto> createImagePostByFiles(List<MultipartFile> files, Integer postId) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        Post post = findPost(postId);

        return files.stream()
                .map(file -> createImagePost(file, post))
                .map(ImagePostResponseDto::from)
                .toList();
    }

    // 게시글 ID 기준으로 저장된 이미지 메타데이터를 조회한다.
    @Transactional(readOnly = true)
    public List<ImagePostResponseDto> getImageByPostId(Integer postId) {
        findPost(postId);

        return imagePostRepository.findAllByPostId(postId).stream()
                .map(ImagePostResponseDto::from)
                .toList();
    }

    // 게시글 ID 기준으로 이미지 URL 문자열만 추출해 반환한다.
    @Transactional(readOnly = true)
    public List<String> getImageUrlsByPostId(Integer postId) {
        return getImageByPostId(postId).stream()
                .map(ImagePostResponseDto::url)
                .toList();
    }

    // 단일 업로드 파일을 디스크에 저장하고 image_post 메타데이터를 생성한다.
    private ImagePost createImagePost(MultipartFile file, Post post) {
        validateFile(file);

        try {
            String fileName = saveFile(file);
            ImagePost imagePost = ImagePost.create(
                    UUID.randomUUID().toString(),
                    ImageUrlVo.from(generateFileUrl(fileName)),
                    post
            );

            return imagePostRepository.save(imagePost);
        } catch (IOException e) {
            throw new IllegalStateException("이미지 파일 저장에 실패했습니다.", e);
        }
    }

    // UUID가 포함된 파일명으로 업로드 디렉터리에 실제 파일을 저장한다.
    private String saveFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + sanitizeFileName(file.getOriginalFilename());
        Path targetDir = Path.of(uploadDir);
        Path targetPath = targetDir.resolve(fileName).normalize();

        Files.createDirectories(targetDir);
        file.transferTo(targetPath);

        return fileName;
    }

    // 저장된 파일명을 브라우저에서 접근 가능한 이미지 URL로 변환한다.
    private String generateFileUrl(String fileName) {
        return serverUrl.replaceAll("/+$", "") + "/images/" + fileName;
    }

    private Post findPost(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        }
    }

    private String sanitizeFileName(String originalFilename) {
        String cleaned = StringUtils.cleanPath(originalFilename == null ? "image" : originalFilename);
        return Path.of(cleaned).getFileName().toString();
    }
}
