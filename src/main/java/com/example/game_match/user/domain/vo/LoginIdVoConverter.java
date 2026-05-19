package com.example.game_match.user.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LoginIdVoConverter implements AttributeConverter<LoginIdVo, String> {
    @Override
    public String convertToDatabaseColumn(LoginIdVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public LoginIdVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : LoginIdVo.from(dbData);
    }
}
