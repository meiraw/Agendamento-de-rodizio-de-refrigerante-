package com.lacoca.lacoquinha.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;
@Data
@NoArgsConstructor
@Entity
@Table(name = "ciclos")
public class CicloModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false )
    private String nome;

    @Column(nullable = false )
    private LocalDate dataInicio;

    @Column(nullable = false )
    private LocalDate dataFim;

    @Column(nullable = false )
    private LocalDate primeiraSexta;

    @ManyToOne
    @JoinColumn(name = "primeiro_responsavel_id", nullable = false)
    private PessoasModel primeiroResponsavel;

    @Column(nullable = false )
    private boolean ativo;
}
