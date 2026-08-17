package com.lacoca.lacoquinha.Service;

import com.lacoca.lacoquinha.DTO.RequestDTO.ParticipanteRequestDTO;
import com.lacoca.lacoquinha.Exception.ResourceNotFoundException;
import com.lacoca.lacoquinha.Model.CicloModel;
import com.lacoca.lacoquinha.Model.ParticipanteModel;
import com.lacoca.lacoquinha.Model.PessoasModel;
import com.lacoca.lacoquinha.Repository.CicloRepository;
import com.lacoca.lacoquinha.Repository.ParticipanteRepository;
import com.lacoca.lacoquinha.Repository.PessoasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service

public class ParticipanteService {

    @Autowired
    private ParticipanteRepository cicloParticipanteRepository;

    @Autowired
    private CicloRepository cicloRepository;

    @Autowired
    private PessoasRepository pessoasRepository;


    public ParticipanteModel adicionar(
            UUID cicloId,
            ParticipanteRequestDTO dto) {

        CicloModel ciclo = cicloRepository.findById(cicloId).orElseThrow(() -> new ResourceNotFoundException("Ciclo não encontrado!"));

        PessoasModel pessoa = pessoasRepository.findById(dto.getPessoaId()).orElseThrow(() ->  new ResourceNotFoundException("Pessoa não encontrada!"));

        List<  ParticipanteModel> participantes =
                cicloParticipanteRepository
                        .findByCicloIdOrderByOrdemAsc(cicloId);

        ParticipanteModel participante =
                new ParticipanteModel();

        participante.setCiclo(ciclo);
        participante.setPessoa(pessoa);

        participante.setOrdem(participantes.size() + 1);

        return cicloParticipanteRepository.save(participante);
    }


    public List<ParticipanteModel> listar(UUID cicloId) {

        return cicloParticipanteRepository
                .findByCicloIdOrderByOrdemAsc(cicloId);
    }
    
}
