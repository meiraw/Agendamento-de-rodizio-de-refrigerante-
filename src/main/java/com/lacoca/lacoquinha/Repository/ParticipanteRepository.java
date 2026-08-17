package com.lacoca.lacoquinha.Repository;

import com.lacoca.lacoquinha.Model.ParticipanteModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipanteRepository extends JpaRepository<ParticipanteModel, UUID> {
    List<ParticipanteModel> findByCicloIdOrderByOrdemAsc(UUID cicloId); //Verificar depois...
}
