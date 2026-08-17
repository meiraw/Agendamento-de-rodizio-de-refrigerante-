package com.lacoca.lacoquinha.DTO.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CicloRequestDTO {

    @NotBlank
    private String nome;

    @NotNull
    private LocalDate dataInicio;

    @NotNull
    private LocalDate primeiraSexta;

    @NotNull
    private UUID primeiroResponsavelId;

}