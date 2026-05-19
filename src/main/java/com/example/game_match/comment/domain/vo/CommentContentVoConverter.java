package com.example.game_match.comment.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CommentContentVoConverter implements AttributeConverter<CommentContentVo, String> {
    @Override
    public String convertToDatabaseColumn(CommentContentVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public CommentContentVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : CommentContentVo.from(dbData);
    }
}
