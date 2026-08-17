package com.lacoca.lacoquinha.DTO.ResponseDTO;

import com.lacoca.lacoquinha.Model.CicloModel;

import java.time.LocalDate;
import java.util.UUID;

public class CicloResponseDTO {
    private UUID id;
    private String nome;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalDate primeiraSexta;

    private UUID primeiroResponsavelId;
    private String primeiroResponsavelNome;
    private String primeiroResponsavelApelido;

    private boolean ativo;

    public CicloResponseDTO(CicloModel model) {

        this.id = model.getId();
        this.nome = model.getNome();
        this.dataInicio = model.getDataInicio();
        this.dataFim = model.getDataFim();
        this.primeiraSexta = model.getPrimeiraSexta();

        this.primeiroResponsavelId =
                model.getPrimeiroResponsavel().getId();

        this.primeiroResponsavelNome =
                model.getPrimeiroResponsavel().getNome();

        this.primeiroResponsavelApelido =
                model.getPrimeiroResponsavel().getApelido();

        this.ativo = model.isAtivo();
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public LocalDate getPrimeiraSexta() {
        return primeiraSexta;
    }

    public UUID getPrimeiroResponsavelId() {
        return primeiroResponsavelId;
    }

    public String getPrimeiroResponsavelNome() {
        return primeiroResponsavelNome;
    }

    public String getPrimeiroResponsavelApelido() {
        return primeiroResponsavelApelido;
    }

    public boolean isAtivo() {
        return ativo;
    }
}

