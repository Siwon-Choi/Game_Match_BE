package com.example.game_match.post.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PostTitleVoConverter implements AttributeConverter<PostTitleVo, String> {
    @Override
    public String convertToDatabaseColumn(PostTitleVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public PostTitleVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PostTitleVo.from(dbData);
    }
}
