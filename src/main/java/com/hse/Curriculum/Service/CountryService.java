package com.hse.Curriculum.Service;

import com.hse.Curriculum.Dto.CountryDTO.*;
import com.hse.Curriculum.Exception.Country.CountryAlreadyExistsException;
import com.hse.Curriculum.Exception.Country.CountryInUseException;
import com.hse.Curriculum.Exception.Country.CountryNotFoundException;
import com.hse.Curriculum.Models.Country;
import com.hse.Curriculum.Repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService {
    private final CountryRepository countryRepository;

    /**
     * Obtener todos los países
     */
    @Transactional(readOnly = true)
    public List<CountryResponseDTO> getAllCountries() {
        log.info("Obteniendo todos los países");
        return countryRepository.findAllByOrderByCountryNameAsc()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener todos los países con paginación
     */
    @Transactional(readOnly = true)
    public Page<CountryResponseDTO> getAllCountries(int page, int size, String sortBy) {
        log.info("Obteniendo países - página: {}, tamaño: {}, ordenar por: {}", page, size, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return countryRepository.findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    /**
     * Obtener listado simple de países (para dropdowns)
     */
    @Transactional(readOnly = true)
    public List<CountrySimpleDTO> getAllCountriesSimple() {
        log.info("Obteniendo listado simple de países");
        return countryRepository.findAllByOrderByCountryNameAsc()
                .stream()
                .map(this::mapToSimpleDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener país por ID
     */
    @Transactional(readOnly = true)
    public CountryResponseDTO getCountryById(Integer id) {
        log.info("Buscando país con ID: {}", id);
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException("País no encontrado con ID: " + id));
        return mapToResponseDTO(country);
    }

    /**
     * Obtener país por código numérico ISO
     */
    @Transactional(readOnly = true)
    public CountryResponseDTO getCountryByCode(Integer countryCode) {
        log.info("Buscando país con código: {}", countryCode);
        Country country = countryRepository.findByCountryCode(countryCode)
                .orElseThrow(() -> new CountryNotFoundException("País no encontrado con código: " + countryCode));
        return mapToResponseDTO(country);
    }

    /**
     * Obtener país por código ISO-2
     */
    @Transactional(readOnly = true)
    public CountryResponseDTO getCountryByIsoCode2(String isoCode2) {
        log.info("Buscando país con código ISO-2: {}", isoCode2);
        Country country = countryRepository.findByIsoCode2(isoCode2.toUpperCase())
                .orElseThrow(() -> new CountryNotFoundException("País no encontrado con código ISO-2: " + isoCode2));
        return mapToResponseDTO(country);
    }

    /**
     * Buscar países por nombre
     */
    @Transactional(readOnly = true)
    public List<CountryResponseDTO> searchCountriesByName(String name) {
        log.info("Buscando países con nombre que contenga: {}", name);
        return countryRepository.findByCountryNameContaining(name)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar países por nombre con paginación
     */
    @Transactional(readOnly = true)
    public Page<CountryResponseDTO> searchCountriesByName(String name, int page, int size) {
        log.info("Buscando países con nombre que contenga: {} - página: {}, tamaño: {}", name, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("countryName").ascending());
        return countryRepository.findByCountryNameContaining(name, pageable)
                .map(this::mapToResponseDTO);
    }

    /**
     * Crear un nuevo país
     */
    @Transactional
    public CountryResponseDTO createCountry(CountryCreateDTO dto) {
        log.info("Creando nuevo país: {}", dto.getCountryName());

        // Validar que no exista el código
        if (countryRepository.existsByCountryCode(dto.getCountryCode())) {
            throw new CountryAlreadyExistsException("Ya existe un país con el código: " + dto.getCountryCode());
        }

        // Validar que no exista el código ISO-2
        if (dto.getIsoCode2() != null && countryRepository.existsByIsoCode2(dto.getIsoCode2())) {
            throw new CountryAlreadyExistsException("Ya existe un país con el código ISO-2: " + dto.getIsoCode2());
        }

        // Validar que no exista el código ISO-3
        if (dto.getIsoCode3() != null && countryRepository.existsByIsoCode3(dto.getIsoCode3())) {
            throw new CountryAlreadyExistsException("Ya existe un país con el código ISO-3: " + dto.getIsoCode3());
        }

        Country country = Country.builder()
                .countryCode(dto.getCountryCode())
                .countryName(dto.getCountryName())
                .isoCode2(dto.getIsoCode2() != null ? dto.getIsoCode2().toUpperCase() : null)
                .isoCode3(dto.getIsoCode3() != null ? dto.getIsoCode3().toUpperCase() : null)
                .phoneCode(dto.getPhoneCode())
                .build();

        Country savedCountry = countryRepository.save(country);
        log.info("País creado exitosamente con ID: {}", savedCountry.getCountryId());
        return mapToResponseDTO(savedCountry);
    }

    /**
     * Actualizar un país existente (PATCH)
     */
    @Transactional
    public CountryResponseDTO updateCountry(Integer id, CountryUpdateDTO dto) {
        log.info("Actualizando país con ID: {}", id);

        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException("País no encontrado con ID: " + id));

        // Actualizar solo los campos que vienen en el DTO
        if (dto.getCountryName() != null) {
            country.setCountryName(dto.getCountryName());
        }

        if (dto.getIsoCode2() != null) {
            // Validar que no exista otro país con ese código
            countryRepository.findByIsoCode2(dto.getIsoCode2().toUpperCase())
                    .ifPresent(existing -> {
                        if (!existing.getCountryId().equals(id)) {
                            throw new CountryAlreadyExistsException("Ya existe otro país con el código ISO-2: " + dto.getIsoCode2());
                        }
                    });
            country.setIsoCode2(dto.getIsoCode2().toUpperCase());
        }

        if (dto.getIsoCode3() != null) {
            // Validar que no exista otro país con ese código
            countryRepository.findByIsoCode3(dto.getIsoCode3().toUpperCase())
                    .ifPresent(existing -> {
                        if (!existing.getCountryId().equals(id)) {
                            throw new CountryAlreadyExistsException("Ya existe otro país con el código ISO-3: " + dto.getIsoCode3());
                        }
                    });
            country.setIsoCode3(dto.getIsoCode3().toUpperCase());
        }

        if (dto.getPhoneCode() != null) {
            country.setPhoneCode(dto.getPhoneCode());
        }

        Country updatedCountry = countryRepository.save(country);
        log.info("País actualizado exitosamente con ID: {}", id);
        return mapToResponseDTO(updatedCountry);
    }

    /**
     * Eliminar un país
     */
    @Transactional
    public void deleteCountry(Integer id) {
        log.info("Eliminando país con ID: {}", id);

        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException("País no encontrado con ID: " + id));

        try {
            countryRepository.delete(country);
            log.info("País eliminado exitosamente con ID: {}", id);
        } catch (Exception e) {
            log.error("Error al eliminar país con ID: {}", id, e);
            throw new CountryInUseException("No se puede eliminar el país porque está siendo usado en otros registros");
        }
    }

    /**
     * Mapear entidad a DTO de respuesta
     */
    private CountryResponseDTO mapToResponseDTO(Country country) {
        return CountryResponseDTO.builder()
                .countryId(country.getCountryId())
                .countryCode(country.getCountryCode())
                .countryName(country.getCountryName())
                .isoCode2(country.getIsoCode2())
                .isoCode3(country.getIsoCode3())
                .phoneCode(country.getPhoneCode())
                .createdAt(country.getCreatedAt())
                .updatedAt(country.getUpdatedAt())
                .build();
    }

    /**
     * Mapear entidad a DTO simple
     */
    private CountrySimpleDTO mapToSimpleDTO(Country country) {
        return CountrySimpleDTO.builder()
                .countryId(country.getCountryId())
                .countryCode(country.getCountryCode())
                .countryName(country.getCountryName())
                .isoCode2(country.getIsoCode2())
                .build();
    }
}
