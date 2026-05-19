package com.example.game_match.game.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GameSortVoConverter implements AttributeConverter<GameSortVo, String> {
    @Override
    public String convertToDatabaseColumn(GameSortVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public GameSortVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : GameSortVo.from(dbData);
    }
}
