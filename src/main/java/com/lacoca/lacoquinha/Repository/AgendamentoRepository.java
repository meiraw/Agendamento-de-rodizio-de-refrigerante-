package com.lacoca.lacoquinha.Repository;

import com.lacoca.lacoquinha.Enum.TipoAgendamento;
import com.lacoca.lacoquinha.Model.AgendamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<AgendamentoModel, UUID> {
    List<AgendamentoModel> findByCicloIdOrderByDataAsc(UUID cicloId);
    List<AgendamentoModel> findByCicloIdAndDataBetweenOrderByDataAsc(
            UUID cicloId,
            LocalDate inicio,
            LocalDate fim
    );
    boolean existsByCicloId(UUID cicloId);

    List<AgendamentoModel> findByCicloIdAndTipoAndDataGreaterThanEqualOrderByDataDesc(
            UUID cicloId,
            TipoAgendamento tipo,
            LocalDate data
    );

}
