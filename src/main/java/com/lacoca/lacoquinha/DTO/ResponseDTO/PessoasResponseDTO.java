package com.lacoca.lacoquinha.DTO.ResponseDTO;

import com.lacoca.lacoquinha.Model.PessoasModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class PessoasResponseDTO {

    private UUID id;
    private String nome;
    private String apelido;

    public PessoasResponseDTO (PessoasModel model){
        this.id = model.getId();
        this.nome = model.getNome();
        this.apelido = model.getApelido();
    }

    public UUID getId(){
        return id;
    }

    public String getNome(){
        return nome;
    }

    public String getApelido(){
        return apelido;
    }
}
