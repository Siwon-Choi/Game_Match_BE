package com.example.game_match.imagepost.domain;

import com.example.game_match.imagepost.domain.vo.ImageUrlVo;
import com.example.game_match.imagepost.domain.vo.ImageUrlVoConverter;
import com.example.game_match.post.domain.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "image_post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImagePost {
    @Id
    @Getter
    @Column(name = "Id", nullable = false, length = 45)
    private String id;

    @Column(name = "URL", nullable = false, length = 255)
    @Convert(converter = ImageUrlVoConverter.class)
    private ImageUrlVo url;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_Id", nullable = false)
    private Post post;

    private ImagePost(String id, ImageUrlVo url, Post post) {
        validateRequired(id, "id");
        validateRequired(url, "url");
        validateRequired(post, "post");

        this.id = id;
        this.url = url;
        this.post = post;
    }

    public static ImagePost create(String id, ImageUrlVo url, Post post) {
        return new ImagePost(id, url, post);
    }

    public String getUrl() {
        return url.getValue();
    }

    public Integer getPostId() {
        return post.getId();
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
