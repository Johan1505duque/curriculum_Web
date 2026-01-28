package com.hse.Curriculum.Converter;

import com.hse.Curriculum.Enum.GraduateStatusEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GraduateStatusConverter implements AttributeConverter<GraduateStatusEnum, String> {

    @Override
    public String convertToDatabaseColumn(GraduateStatusEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public GraduateStatusEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty() ) {
            return null;
        }
        return GraduateStatusEnum.valueOf(dbData);
    }
}
