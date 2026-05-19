package com.example.game_match.user.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserNameVoConverter implements AttributeConverter<UserNameVo, String> {
    @Override
    public String convertToDatabaseColumn(UserNameVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public UserNameVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : UserNameVo.from(dbData);
    }
}
