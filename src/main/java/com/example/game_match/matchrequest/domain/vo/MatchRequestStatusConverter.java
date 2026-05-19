package com.example.game_match.matchrequest.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MatchRequestStatusConverter implements AttributeConverter<MatchRequestStatus, String> {
    @Override
    public String convertToDatabaseColumn(MatchRequestStatus attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public MatchRequestStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        if ("confirm".equals(dbData)) {
            return MatchRequestStatus.approve;
        }

        return MatchRequestStatus.valueOf(dbData);
    }
}
