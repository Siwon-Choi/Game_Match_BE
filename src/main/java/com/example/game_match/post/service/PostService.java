package com.example.game_match.post.service;

import com.example.game_match.comment.repository.CommentRepository;
import com.example.game_match.game.domain.Game;
import com.example.game_match.game.domain.vo.GameNameVo;
import com.example.game_match.game.repository.GameRepository;
import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.imagepost.repository.ImagePostRepository;
import com.example.game_match.post.domain.Post;
import com.example.game_match.post.domain.vo.PostContentVo;
import com.example.game_match.post.domain.vo.PostTitleVo;
import com.example.game_match.post.dto.PostCreateDto;
import com.example.game_match.post.dto.PostResponseDto;
import com.example.game_match.post.dto.PostUpdateDto;
import com.example.game_match.post.repository.PostRepository;
import com.example.game_match.recommendation.repository.PostRecommendationRepository;
import com.example.game_match.user.domain.User;
import com.example.game_match.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final CommentRepository commentRepository;
    private final ImagePostRepository imagePostRepository;
    private final PostRecommendationRepository postRecommendationRepository;

    // 게시글을 생성한다.
    @Transactional
    public PostResponseDto createPost(PostCreateDto dto) {
        User user = findUser(dto.userId());
        Game game = findGame(dto.gameId());

        Post post = Post.create(
                PostTitleVo.from(dto.title()),
                PostContentVo.from(dto.content()),
                user,
                game,
                Boolean.TRUE.equals(dto.anonymous())
        );

        return PostResponseDto.from(postRepository.save(post));
    }

    // 게시글 ID 기준으로 게시글을 수정한다.
    @Transactional
    public PostResponseDto updatePost(Integer postId, PostUpdateDto dto) {
        Post post = findPostById(postId);
        User user = findUser(dto.userId());
        Game game = findGame(dto.gameId());

        post.update(
                PostTitleVo.from(dto.title()),
                PostContentVo.from(dto.content()),
                user,
                game,
                Boolean.TRUE.equals(dto.anonymous())
        );

        return PostResponseDto.from(post);
    }

    // 게시글 ID 기준으로 게시글을 삭제한다.
    @Transactional
    public void deletePost(Integer postId) {
        findPostById(postId);
        deletePostChildren(postId);
        postRepository.deleteById(postId);
    }

    // 게시글 ID 기준으로 상세 정보를 조회한다.
    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Integer postId) {
        return PostResponseDto.from(findPostById(postId));
    }

    // 게시글 조회수를 1 증가시킨다.
    @Transactional
    public void incrementViews(Integer postId) {
        findPostById(postId);
        postRepository.incrementViews(postId);
    }

    // 이미 조회된 Game 엔티티와 Pageable 조건으로 게시글 페이지를 조회한다.
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPosts(Game game, Pageable pageable) {
        return postRepository.findByGame(game, pageable)
                .map(PostResponseDto::from);
    }

    // 게임 이름 기준으로 게시글 목록을 페이지 단위로 조회한다.
    // 최신 글이 먼저 보이도록 date, time, id 역순으로 정렬한다.
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostsByGameName(String gameName, int page, int size) {
        Game game = gameRepository.findByName(GameNameVo.from(gameName))
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));
        Pageable pageable = PageRequest.of(
                normalizePage(page),
                normalizeSize(size),
                Sort.by(Sort.Direction.DESC, "date", "time", "id")
        );

        return getPosts(game, pageable);
    }

    // postId로 Post 엔티티를 조회한다.
    @Transactional(readOnly = true)
    public Post findPostById(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    // userId로 작성자 엔티티를 조회한다.
    private User findUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    // gameId로 게시글이 속할 게임 엔티티를 조회한다.
    private Game findGame(Integer gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));
    }

    private int normalizePage(int page) {
        return Math.max(page, DEFAULT_PAGE);
    }

    private int normalizeSize(int size) {
        return size < 1 ? DEFAULT_SIZE : size;
    }

    private void deletePostChildren(Integer postId) {
        commentRepository.deleteRepliesByPostId(postId);
        commentRepository.deleteTopLevelCommentsByPostId(postId);
        imagePostRepository.deleteByPostId(postId);
        postRecommendationRepository.deleteByPostId(postId);
    }
}
