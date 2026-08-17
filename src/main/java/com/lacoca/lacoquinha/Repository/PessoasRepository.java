package com.lacoca.lacoquinha.Repository;

import com.lacoca.lacoquinha.Model.PessoasModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PessoasRepository extends JpaRepository<PessoasModel, UUID> {
}
