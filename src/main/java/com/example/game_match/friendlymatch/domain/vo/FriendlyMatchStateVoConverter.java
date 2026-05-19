package com.example.game_match.friendlymatch.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FriendlyMatchStateVoConverter implements AttributeConverter<FriendlyMatchStateVo, Byte> {
    @Override
    public Byte convertToDatabaseColumn(FriendlyMatchStateVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public FriendlyMatchStateVo convertToEntityAttribute(Byte dbData) {
        return dbData == null ? null : FriendlyMatchStateVo.from(dbData);
    }
}
