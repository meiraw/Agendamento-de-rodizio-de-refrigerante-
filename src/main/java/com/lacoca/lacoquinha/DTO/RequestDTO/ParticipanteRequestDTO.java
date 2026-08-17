package com.lacoca.lacoquinha.DTO.RequestDTO;

import com.lacoca.lacoquinha.Model.CicloModel;
import com.lacoca.lacoquinha.Model.PessoasModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data

public class ParticipanteRequestDTO {

    @NotNull
    private UUID pessoaId;

}
