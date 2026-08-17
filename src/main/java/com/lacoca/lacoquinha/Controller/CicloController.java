package com.lacoca.lacoquinha.Controller;


import com.lacoca.lacoquinha.DTO.RequestDTO.CicloRequestDTO;
import com.lacoca.lacoquinha.DTO.ResponseDTO.CicloResponseDTO;
import com.lacoca.lacoquinha.DTO.ResponseDTO.PessoasResponseDTO;
import com.lacoca.lacoquinha.Model.CicloModel;
import com.lacoca.lacoquinha.Service.CicloService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ciclos")
public class CicloController {
    @Autowired
    private CicloService cicloService;

    @PostMapping
    public ResponseEntity<CicloResponseDTO> criar(@Valid @RequestBody CicloRequestDTO dto) {
        CicloModel ciclo = cicloService.salvar(dto);
        return ResponseEntity.status(201).body(new CicloResponseDTO(ciclo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CicloResponseDTO> buscar(@PathVariable UUID id ){
        CicloModel buscar = cicloService.buscaPorId(id);
        return ResponseEntity.ok(new CicloResponseDTO(buscar));
    }

    @GetMapping
    public ResponseEntity<List<CicloResponseDTO>> listar() {

        List<CicloResponseDTO> listar = cicloService.listarTudo()
                .stream()
                .map(CicloResponseDTO::new).toList();

        return ResponseEntity.ok(listar);
    }
}
