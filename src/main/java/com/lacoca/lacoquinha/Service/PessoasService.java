package com.lacoca.lacoquinha.Service;

import com.lacoca.lacoquinha.DTO.RequestDTO.PessoasRequestDTO;
import com.lacoca.lacoquinha.Exception.ResourceNotFoundException;
import com.lacoca.lacoquinha.Model.PessoasModel;
import com.lacoca.lacoquinha.Repository.PessoasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PessoasService {

    @Autowired
    private PessoasRepository pessoasrepository;

    public PessoasModel salvar ( PessoasRequestDTO dto){

        PessoasModel pessoas = new PessoasModel();
        pessoas.setNome(dto.getNome());
        pessoas.setApelido(dto.getApelido());
        return pessoasrepository.save(pessoas);
    }

    public List<PessoasModel> listarpessoas (){
        return pessoasrepository.findAll();
    }

    public PessoasModel buscarPorId (UUID id){
        return pessoasrepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("O id "+id+" não foi encontrado!"));
    }


    public PessoasModel atualizar (PessoasRequestDTO dto , UUID id ){
        PessoasModel novapessoa = buscarPorId(id);
        novapessoa.setNome(dto.getNome());
        novapessoa.setApelido(dto.getApelido());
        return pessoasrepository.save(novapessoa);
    }

    public void deletar (UUID id){
        PessoasModel excluir = buscarPorId (id);
        pessoasrepository.delete(excluir);
    }
}
