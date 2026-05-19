package com.example.game_match.gameuser.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GameUserNicknameVoConverter implements AttributeConverter<GameUserNicknameVo, String> {
    @Override
    public String convertToDatabaseColumn(GameUserNicknameVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public GameUserNicknameVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : GameUserNicknameVo.from(dbData);
    }
}
