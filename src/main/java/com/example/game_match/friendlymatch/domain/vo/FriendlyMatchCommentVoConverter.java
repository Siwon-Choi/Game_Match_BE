package com.example.game_match.friendlymatch.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FriendlyMatchCommentVoConverter implements AttributeConverter<FriendlyMatchCommentVo, String> {
    @Override
    public String convertToDatabaseColumn(FriendlyMatchCommentVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public FriendlyMatchCommentVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : FriendlyMatchCommentVo.from(dbData);
    }
}
