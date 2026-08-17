package com.lacoca.lacoquinha.Controller;

import com.lacoca.lacoquinha.DTO.RequestDTO.PessoasRequestDTO;
import com.lacoca.lacoquinha.DTO.ResponseDTO.PessoasResponseDTO;
import com.lacoca.lacoquinha.Model.PessoasModel;
import com.lacoca.lacoquinha.Service.PessoasService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pessoas")

public class PessoasController {

    @Autowired
    private PessoasService pessoasservice;

    @PostMapping
    public ResponseEntity<PessoasResponseDTO> criar (@Valid @RequestBody PessoasRequestDTO dto){
        PessoasModel pessoas = pessoasservice.salvar(dto);
        return ResponseEntity.status(201).body( new PessoasResponseDTO(pessoas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoasResponseDTO> buscaporid (@PathVariable UUID id ){
        PessoasModel buscar = pessoasservice.buscarPorId(id);
        return ResponseEntity.ok(new PessoasResponseDTO(buscar));
    }

    @GetMapping
    public ResponseEntity<List<PessoasResponseDTO>> listar() {

        List<PessoasResponseDTO> listar = pessoasservice.listarpessoas()
                .stream()
                .map(PessoasResponseDTO::new)
                .toList();

        return ResponseEntity.ok(listar);
    }

    @PutMapping("/{id}")
    public ResponseEntity <PessoasResponseDTO> atualizar (@Valid @RequestBody PessoasRequestDTO dto ,@PathVariable UUID id  ){
        PessoasModel novapessoa = pessoasservice.atualizar(dto,id);
        return ResponseEntity.ok(new PessoasResponseDTO(novapessoa));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        pessoasservice.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
