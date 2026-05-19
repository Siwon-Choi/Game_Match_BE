package com.example.game_match.game.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GameNameVoConverter implements AttributeConverter<GameNameVo, String> {
    @Override
    public String convertToDatabaseColumn(GameNameVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public GameNameVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : GameNameVo.from(dbData);
    }
}
