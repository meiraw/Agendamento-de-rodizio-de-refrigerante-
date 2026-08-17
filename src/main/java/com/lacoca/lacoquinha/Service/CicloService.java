package com.lacoca.lacoquinha.Service;

import com.lacoca.lacoquinha.DTO.RequestDTO.CicloRequestDTO;
import com.lacoca.lacoquinha.Exception.ResourceNotFoundException;
import com.lacoca.lacoquinha.Model.CicloModel;
import com.lacoca.lacoquinha.Model.PessoasModel;
import com.lacoca.lacoquinha.Repository.CicloRepository;
import com.lacoca.lacoquinha.Repository.PessoasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CicloService {

    @Autowired
    private CicloRepository cicloRepository;

    @Autowired
    private PessoasRepository pessoasRepository;

    public CicloModel salvar(CicloRequestDTO dto) {

        PessoasModel pessoa = pessoasRepository.findById(dto.getPrimeiroResponsavelId()).orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada!"));
        CicloModel ciclo = new CicloModel();

        ciclo.setNome(dto.getNome());
        ciclo.setDataInicio(dto.getDataInicio());

        LocalDate dataFim = dto.getDataInicio()
                .plusYears(1)
                .minusDays(1);

        ciclo.setDataFim(dataFim);

        ciclo.setPrimeiraSexta(dto.getPrimeiraSexta());
        ciclo.setPrimeiroResponsavel(pessoa);
        ciclo.setAtivo(true);

        return cicloRepository.save(ciclo);
    }

    public CicloModel buscaPorId(UUID id) {
        return cicloRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("O id " + id + " não foi encontrado!"));
    }

    public List<CicloModel> listarTudo(){
        return cicloRepository.findAll();
    }
}

