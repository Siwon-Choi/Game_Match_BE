package com.example.game_match.auth.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TokenHashVoConverter implements AttributeConverter<TokenHashVo, String> {
    @Override
    public String convertToDatabaseColumn(TokenHashVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TokenHashVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TokenHashVo.from(dbData);
    }
}
