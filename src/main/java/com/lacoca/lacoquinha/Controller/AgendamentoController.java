package com.lacoca.lacoquinha.Controller;

import com.lacoca.lacoquinha.DTO.ResponseDTO.AgendamentoResponseDTO;
import com.lacoca.lacoquinha.Model.AgendamentoModel;


import com.lacoca.lacoquinha.Service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agendamentos")

public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;


    @PostMapping("/gerar/{cicloId}")
    public ResponseEntity<List<AgendamentoResponseDTO>> gerar(
            @PathVariable UUID cicloId) {

        List<AgendamentoModel> agendamentos =
                agendamentoService.gerarAgendamentos(cicloId);

        List<AgendamentoResponseDTO> response =
                agendamentos.stream()
                        .map(AgendamentoResponseDTO::new)
                        .toList();

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/ciclo/{cicloId}")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarPorCiclo(
            @PathVariable UUID cicloId) {

        List<AgendamentoResponseDTO> agendamentos =
                agendamentoService.listarPorCiclo(cicloId)
                        .stream()
                        .map(AgendamentoResponseDTO::new)
                        .toList();

        return ResponseEntity.ok(agendamentos);
    }
    @PutMapping("/{agendamentoId}/pagar")
    public ResponseEntity<AgendamentoResponseDTO> marcarComoPago(
            @PathVariable UUID agendamentoId) {

        AgendamentoModel agendamento =
                agendamentoService.marcarComoPago(agendamentoId);

        return ResponseEntity.ok(
                new AgendamentoResponseDTO(agendamento)
        );
    }

    @PutMapping("/{agendamentoId}/adiar")
    public ResponseEntity<AgendamentoResponseDTO> adiar(
            @PathVariable UUID agendamentoId) {

        AgendamentoModel agendamento =
                agendamentoService.adiarAgendamento(agendamentoId);

        return ResponseEntity.ok(
                new AgendamentoResponseDTO(agendamento)
        );
    }
}
