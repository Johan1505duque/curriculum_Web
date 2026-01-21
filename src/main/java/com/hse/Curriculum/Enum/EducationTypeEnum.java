package com.hse.Curriculum.Enum;


public enum EducationTypeEnum {
    PRIMARIA("primaria"),
    MEDIA("media"),
    SUPERIOR("superior");

    private final String value;

    EducationTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EducationTypeEnum fromValue(String value) {
        for (EducationTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de educación inválido: " + value);
    }
}
