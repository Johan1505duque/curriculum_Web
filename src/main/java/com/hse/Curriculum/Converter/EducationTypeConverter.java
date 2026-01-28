package com.hse.Curriculum.Converter;

import com.hse.Curriculum.Enum.EducationTypeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EducationTypeConverter implements AttributeConverter<EducationTypeEnum, String> {

    @Override
    public String convertToDatabaseColumn(EducationTypeEnum attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public EducationTypeEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        for (EducationTypeEnum type : EducationTypeEnum.values()) {
            if (type.getValue().equals(dbData)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Valor incorrecto: " + dbData);
    }
}