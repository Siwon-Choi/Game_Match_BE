package com.example.game_match.imagepost.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ImageUrlVoConverter implements AttributeConverter<ImageUrlVo, String> {
    @Override
    public String convertToDatabaseColumn(ImageUrlVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ImageUrlVo convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ImageUrlVo.from(dbData);
    }
}
