package com.example.game_match.user.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRegisterRequestDto {
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate birth;
    private String loginId;
    private String loginPassword;

    public UserRegisterDto toServiceDto() {
        return new UserRegisterDto(name, email, phoneNumber, birth, loginId, loginPassword);
    }
}
