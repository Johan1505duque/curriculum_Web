package com.hse.Curriculum.Converter.Training;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;


/**
 * Deserializador personalizado para LocalDate que acepta múltiples formatos
 * Permite que los usuarios envíen fechas en diferentes formatos y las convierte automáticamente
 */
public class FlexibleDateDeserializer  extends JsonDeserializer<LocalDate> {
    /**
     * Lista de formatos de fecha aceptados (en orden de prioridad)
     */
    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
            // Formatos con separadores
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),      // 25/12/2024
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),      // 25-12-2024
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),      // 2024-12-25 (ISO)
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),      // 2024/12/25
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),      // 25.12.2024

            // Formatos sin separadores
            DateTimeFormatter.ofPattern("ddMMyyyy"),        // 25122024
            DateTimeFormatter.ofPattern("yyyyMMdd"),        // 20241225

            // Formatos con nombres de mes
            DateTimeFormatter.ofPattern("dd MMM yyyy"),     // 25 Dec 2024
            DateTimeFormatter.ofPattern("dd MMMM yyyy"),    // 25 December 2024

            // Formatos americanos
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),      // 12/25/2024
            DateTimeFormatter.ofPattern("MM-dd-yyyy"),      // 12-25-2024

            // ISO 8601
            DateTimeFormatter.ISO_LOCAL_DATE                // 2024-12-25
    );


    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String dateString = parser.getText().trim();

        if (dateString.isEmpty()) {
            return null;
        }

        // Intentar parsear con cada formato
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // Continuar con el siguiente formato
            }
        }

        // Si ningún formato funciona, lanzar excepción descriptiva
        throw new IOException(String.format(
                "No se pudo parsear la fecha '%s'. Formatos aceptados: " +
                        "dd/MM/yyyy, dd-MM-yyyy, yyyy-MM-dd, yyyy/MM/dd, dd.MM.yyyy, " +
                        "ddMMyyyy, yyyyMMdd, MM/dd/yyyy, MM-dd-yyyy, ISO 8601",
                dateString
        ));
    }
}
