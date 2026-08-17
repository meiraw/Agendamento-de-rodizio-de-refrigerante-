package com.lacoca.lacoquinha.DTO.ResponseDTO;

import com.lacoca.lacoquinha.Model.ParticipanteModel;

import java.util.UUID;

public class ParticipanteResponseDTO {

    private UUID id;
    private UUID pessoaId;
    private String pessoanome;
    private String pessoaapelido;
    private Integer ordem;

    public ParticipanteResponseDTO (ParticipanteModel model){
        this.id = model.getId();
        this.pessoaId = model.getPessoa().getId();
        this.pessoanome = model.getPessoa().getNome();
        this.pessoaapelido = model.getPessoa().getApelido();
        this.ordem = model.getOrdem();
    }

    public UUID getId() {
        return id;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public String getPessoaapelido() {
        return pessoaapelido;
    }

    public UUID getPessoaid() {
        return pessoaId;
    }

    public String getPessoanome() {
        return pessoanome;
    }
}
