package com.lacoca.lacoquinha.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lacoca.lacoquinha.Enum.StatusAgendamento;
import com.lacoca.lacoquinha.Enum.TipoAgendamento;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class AgendamentoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false )
    private TipoAgendamento tipo;

    @ManyToOne
    @JoinColumn(name = "ciclo_id",nullable = false )
    private CicloModel ciclo;

    @ManyToOne
    @JoinColumn(name = "participante_id")
    private ParticipanteModel participante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;

}
