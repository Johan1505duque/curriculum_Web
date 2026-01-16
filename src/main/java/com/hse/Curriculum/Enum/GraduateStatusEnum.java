package com.hse.Curriculum.Enum;


public enum GraduateStatusEnum {
    NO("no"),
    EN_CURSO("en_curso"),
    GRADUADO("graduado");

    private final String value;

    GraduateStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static GraduateStatusEnum fromValue(String value) {
        for (GraduateStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Valor inválido: " + value);
    }
}
