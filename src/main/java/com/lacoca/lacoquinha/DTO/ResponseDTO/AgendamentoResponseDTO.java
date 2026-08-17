package com.lacoca.lacoquinha.DTO.ResponseDTO;

import com.lacoca.lacoquinha.Enum.StatusAgendamento;
import com.lacoca.lacoquinha.Enum.TipoAgendamento;
import com.lacoca.lacoquinha.Model.AgendamentoModel;

import java.time.LocalDate;
import java.util.UUID;

public class AgendamentoResponseDTO {

    private UUID id;
    private LocalDate data;
    private TipoAgendamento tipo;
    private StatusAgendamento status;
    private UUID cicloId;
    private UUID participanteId;
    private Integer ordem;

    private UUID pessoaId;
    private String pessoaNome;
    private String pessoaApelido;


    public AgendamentoResponseDTO(AgendamentoModel model) {

        this.id = model.getId();
        this.data = model.getData();
        this.tipo = model.getTipo();
        this.status = model.getStatus();
        this.cicloId = model.getCiclo().getId();

        if (model.getParticipante() != null) {

            this.participanteId =
                    model.getParticipante().getId();

            this.ordem =
                    model.getParticipante().getOrdem();

            this.pessoaId =
                    model.getParticipante().getPessoa().getId();

            this.pessoaNome =
                    model.getParticipante().getPessoa().getNome();

            this.pessoaApelido =
                    model.getParticipante().getPessoa().getApelido();
        }
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public TipoAgendamento getTipo() {
        return tipo;
    }

    public StatusAgendamento getStatus(){
        return status;
    }

    public UUID getCicloId() {
        return cicloId;
    }

    public UUID getParticipanteId() {
        return participanteId;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public UUID getPessoaId() {
        return pessoaId;
    }

    public String getPessoaNome() {
        return pessoaNome;
    }

    public String getPessoaApelido() {
        return pessoaApelido;
    }
}
