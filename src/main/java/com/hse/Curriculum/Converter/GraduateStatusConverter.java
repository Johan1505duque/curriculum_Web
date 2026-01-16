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
        return attribute.getValue();
    }

    @Override
    public GraduateStatusEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return GraduateStatusEnum.fromValue(dbData);
    }
}
