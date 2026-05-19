package com.example.game_match.user.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PhoneNumberVoConverter implements AttributeConverter<PhoneNumberVo, String> {
    @Override
    public String convertToDatabaseColumn(PhoneNumberVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public PhoneNumberVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PhoneNumberVo.from(dbData);
    }
}
