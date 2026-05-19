package com.example.game_match.gamegroup.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GameGroupNameVoConverter implements AttributeConverter<GameGroupNameVo, String> {
    @Override
    public String convertToDatabaseColumn(GameGroupNameVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public GameGroupNameVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : GameGroupNameVo.from(dbData);
    }
}
