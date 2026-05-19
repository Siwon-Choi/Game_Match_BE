package com.example.game_match.game.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GameUrlVoConverter implements AttributeConverter<GameUrlVo, String> {
    @Override
    public String convertToDatabaseColumn(GameUrlVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public GameUrlVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : GameUrlVo.from(dbData);
    }
}
