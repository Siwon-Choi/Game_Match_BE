package com.example.game_match.user.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmailVoConverter implements AttributeConverter<EmailVo, String> {
    @Override
    public String convertToDatabaseColumn(EmailVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public EmailVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : EmailVo.from(dbData);
    }
}
