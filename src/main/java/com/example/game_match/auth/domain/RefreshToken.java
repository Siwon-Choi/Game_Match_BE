package com.example.game_match.auth.domain;

import com.example.game_match.auth.domain.vo.TokenHashVo;
import com.example.game_match.auth.domain.vo.TokenHashVoConverter;
import com.example.game_match.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// JPA가 관리할 수 있는 엔티티
@Entity
@Table(name = "Refresh_Token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @OneToOne
    @JoinColumn(name = "User_Id", nullable = false, unique = true)
    private User user;

    @Column(name = "Token_Hash", nullable = false, length = 128)
    @Convert(converter = TokenHashVoConverter.class)
    private TokenHashVo tokenHash;

    private RefreshToken(User user, TokenHashVo tokenHash) {
        validateRequiredUser(user);
        validateRequiredTokenHash(tokenHash);

        this.user = user;
        this.tokenHash = tokenHash;
    }

    public static RefreshToken create(User user, TokenHashVo tokenHash) {
        return new RefreshToken(user, tokenHash);
    }


    // 토큰 교환
    public void updateTokenHash(TokenHashVo tokenHash) {
        validateRequiredTokenHash(tokenHash);
        this.tokenHash = tokenHash;
    }


    public String getTokenHash() {
        return tokenHash.getValue();
    }

    private static void validateRequiredUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user는 필수입니다.");
        }
    }

    private static void validateRequiredTokenHash(TokenHashVo tokenHash) {
        if (tokenHash == null) {
            throw new IllegalArgumentException("tokenHash는 필수입니다.");
        }
    }
}
