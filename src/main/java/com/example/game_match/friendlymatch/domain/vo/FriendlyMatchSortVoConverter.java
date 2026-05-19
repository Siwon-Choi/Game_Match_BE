package com.example.game_match.friendlymatch.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FriendlyMatchSortVoConverter implements AttributeConverter<FriendlyMatchSortVo, Byte> {
    @Override
    public Byte convertToDatabaseColumn(FriendlyMatchSortVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public FriendlyMatchSortVo convertToEntityAttribute(Byte dbData) {
        return dbData == null ? null : FriendlyMatchSortVo.from(dbData);
    }
}
