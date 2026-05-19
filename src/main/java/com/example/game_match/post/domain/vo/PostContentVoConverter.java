package com.example.game_match.post.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PostContentVoConverter implements AttributeConverter<PostContentVo, String> {
    @Override
    public String convertToDatabaseColumn(PostContentVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public PostContentVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PostContentVo.from(dbData);
    }
}
