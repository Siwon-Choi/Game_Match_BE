package com.example.game_match.friendlymatch.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FriendlyMatchRecruitVoConverter implements AttributeConverter<FriendlyMatchRecruitVo, Integer> {
    @Override
    public Integer convertToDatabaseColumn(FriendlyMatchRecruitVo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public FriendlyMatchRecruitVo convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : FriendlyMatchRecruitVo.from(dbData);
    }
}
