package com.example.game_match.user.domain;

import com.example.game_match.user.domain.vo.EncodedPasswordVo;
import com.example.game_match.user.domain.vo.EncodedPasswordVoConverter;
import com.example.game_match.user.domain.vo.EmailVo;
import com.example.game_match.user.domain.vo.EmailVoConverter;
import com.example.game_match.user.domain.vo.LoginIdVo;
import com.example.game_match.user.domain.vo.LoginIdVoConverter;
import com.example.game_match.user.domain.vo.PhoneNumberVo;
import com.example.game_match.user.domain.vo.PhoneNumberVoConverter;
import com.example.game_match.user.domain.vo.UserNameVo;
import com.example.game_match.user.domain.vo.UserNameVoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "User")
// JPA가 엔티티를 DB에서 꺼낼 때 기본 생성자가 필요한데,
// public이면 아무곳에서나 생성이 가능해지기 때문이다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private static final int PROFILE_MAX_LENGTH = 255;

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Name", nullable = false, length = 255)
    @Convert(converter = UserNameVoConverter.class)
    private UserNameVo name;

    @Column(name = "Email", nullable = false, unique = true, length = 255)
    @Convert(converter = EmailVoConverter.class)
    private EmailVo email;

    @Getter
    @Column(name = "Profile", length = 255)
    private String profile;

    @Column(name = "Phone_Number", nullable = false, unique = true, length = 20)
    @Convert(converter = PhoneNumberVoConverter.class)
    private PhoneNumberVo phoneNumber;

    @Getter
    @Column(name = "Birth", nullable = false)
    private LocalDate birth;

    @Column(name = "Login_Id", nullable = false, unique = true, length = 255)
    @Convert(converter = LoginIdVoConverter.class)
    private LoginIdVo loginId;

    @Column(name = "Login_Password", nullable = false, length = 255)
    @Convert(converter = EncodedPasswordVoConverter.class)
    private EncodedPasswordVo loginPassword;


    private User(
            UserNameVo name,
            EmailVo email,
            String profile,
            PhoneNumberVo phoneNumber,
            LocalDate birth,
            LoginIdVo loginId,
            EncodedPasswordVo loginPassword
    ) {
        validateRequired(name, "name");
        validateRequired(email, "email");
        validateProfile(profile);
        validateRequired(phoneNumber, "phoneNumber");
        validateRequired(birth, "birth");
        validateRequired(loginId, "loginId");
        validateRequired(loginPassword, "loginPassword");

        this.name = name;
        this.email = email;
        this.profile = profile == null ? "" : profile;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
        this.loginId = loginId;
        this.loginPassword = loginPassword;
    }

    public static User create(
            UserNameVo name,
            EmailVo email,
            PhoneNumberVo phoneNumber,
            LocalDate birth,
            LoginIdVo loginId,
            EncodedPasswordVo loginPassword
    ) {
        return new User(name, email, "", phoneNumber, birth, loginId, loginPassword);
    }



    public void updateProfile(String profile) {
        validateProfile(profile);
        this.profile = profile == null ? "" : profile;
    }

    public String getName() {
        return name.getValue();
    }

    public String getEmail() {
        return email.getValue();
    }

    public String getPhoneNumber() {
        return phoneNumber.getValue();
    }

    public String getLoginId() {
        return loginId.getValue();
    }

    public String getLoginPassword() {
        return loginPassword.getValue();
    }


    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }

    private static void validateProfile(String profile) {
        if (profile != null && profile.length() > PROFILE_MAX_LENGTH) {
            throw new IllegalArgumentException("profile은 255자 이하여야 합니다.");
        }
    }
}
