package com.example.game_match.user.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EncodedPasswordVoConverter implements AttributeConverter<EncodedPasswordVo, String> {
    @Override
    public String convertToDatabaseColumn(EncodedPasswordVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public EncodedPasswordVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : EncodedPasswordVo.from(dbData);
    }
}
