package com.lacoca.lacoquinha.Controller;

import com.lacoca.lacoquinha.DTO.RequestDTO.ParticipanteRequestDTO;
import com.lacoca.lacoquinha.DTO.ResponseDTO.ParticipanteResponseDTO;
import com.lacoca.lacoquinha.Model.ParticipanteModel;
import com.lacoca.lacoquinha.Service.ParticipanteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ciclos")
public class ParticipanteController {

    @Autowired
    private ParticipanteService ParticipanteService;


    @PostMapping("/{cicloId}/participantes")
    public ResponseEntity<ParticipanteResponseDTO> adicionar(@PathVariable UUID cicloId, @Valid @RequestBody ParticipanteRequestDTO dto) {

        ParticipanteModel participante = ParticipanteService.adicionar(cicloId, dto);
        return ResponseEntity.status(201).body(new ParticipanteResponseDTO(participante));
    }


    @GetMapping("/{cicloId}/participantes")
    public ResponseEntity<List<ParticipanteResponseDTO>> listar(@PathVariable UUID cicloId) {

        List<ParticipanteResponseDTO> participantes = ParticipanteService.listar(cicloId).stream().map(ParticipanteResponseDTO::new).toList();
        return ResponseEntity.ok(participantes);
    }
}
