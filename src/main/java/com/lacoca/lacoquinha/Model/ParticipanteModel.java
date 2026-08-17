package com.lacoca.lacoquinha.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Entity
@Data
@NoArgsConstructor
public class ParticipanteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "Ciclo_id", nullable = false)
    private CicloModel ciclo;

    @ManyToOne
    @JoinColumn(name = "Pessoa_id", nullable = false)
    private PessoasModel pessoa;

    @Column(nullable = false)
    private Integer ordem;
}
