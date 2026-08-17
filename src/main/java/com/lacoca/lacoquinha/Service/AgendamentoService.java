package com.lacoca.lacoquinha.Service;

import com.lacoca.lacoquinha.Enum.StatusAgendamento;
import com.lacoca.lacoquinha.Enum.TipoAgendamento;
import com.lacoca.lacoquinha.Exception.ResourceNotFoundException;
import com.lacoca.lacoquinha.Model.AgendamentoModel;
import com.lacoca.lacoquinha.Model.CicloModel;
import com.lacoca.lacoquinha.Model.ParticipanteModel;
import com.lacoca.lacoquinha.Repository.AgendamentoRepository;
import com.lacoca.lacoquinha.Repository.CicloRepository;
import com.lacoca.lacoquinha.Repository.ParticipanteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service

public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private CicloRepository cicloRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;


    public List<AgendamentoModel> gerarAgendamentos(UUID cicloId ){
        CicloModel ciclo = cicloRepository.findById(cicloId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ciclo não encontrado!"));

        if (agendamentoRepository.existsByCicloId(cicloId)) {
            throw new IllegalStateException(
                    "Os agendamentos deste ciclo já foram gerados!"
            );
        }

        List<ParticipanteModel> participantes = participanteRepository.findByCicloIdOrderByOrdemAsc(cicloId);

        if (participantes.isEmpty()) {
            throw new ResourceNotFoundException(
                    "O ciclo não possui participantes!");
        }

        List<AgendamentoModel> agendamentos = new ArrayList<>();

        LocalDate data = ciclo.getDataInicio();
        LocalDate dataFim = ciclo.getDataFim();

        int indiceParticipante = 0;

        while (!data.isAfter(dataFim)) {

            // Segunda-feira = COTA
            if (data.getDayOfWeek() == DayOfWeek.MONDAY) {

                AgendamentoModel agendamento = new AgendamentoModel();

                agendamento.setData(data);
                agendamento.setTipo(TipoAgendamento.COTA);
                agendamento.setStatus(StatusAgendamento.PENDENTE);
                agendamento.setCiclo(ciclo);
                agendamento.setParticipante(null);

                agendamentos.add(agendamento);
            }

            // Sexta-feira = PAGAMENTO
            if (data.getDayOfWeek() == DayOfWeek.FRIDAY) {

                AgendamentoModel agendamento = new AgendamentoModel();

                agendamento.setData(data);
                agendamento.setTipo(TipoAgendamento.PAGAMENTO);
                agendamento.setStatus(StatusAgendamento.PENDENTE);
                agendamento.setCiclo(ciclo);

                ParticipanteModel participante =
                        participantes.get(indiceParticipante);

                agendamento.setParticipante(participante);

                agendamentos.add(agendamento);

                indiceParticipante++;

                // Voltou para o primeiro participante
                if (indiceParticipante >= participantes.size()) {
                    indiceParticipante = 0;
                }
            }

            data = data.plusDays(1);
        }

        return agendamentoRepository.saveAll(agendamentos);
    }
    public List<AgendamentoModel> listarPorCiclo(UUID cicloId) {

        cicloRepository.findById(cicloId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ciclo não encontrado!"));

        return agendamentoRepository
                .findByCicloIdOrderByDataAsc(cicloId);
    }

    public AgendamentoModel marcarComoPago(UUID agendamentoId) {

        AgendamentoModel agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Agendamento não encontrado!"));

        if (agendamento.getTipo() != TipoAgendamento.PAGAMENTO) {
            throw new IllegalStateException(
                    "Somente agendamentos de pagamento podem ser marcados como pagos!"
            );
        }

        if (agendamento.getStatus() == StatusAgendamento.PAGO) {
            throw new IllegalStateException(
                    "Este pagamento já foi realizado!"
            );
        }

        agendamento.setStatus(StatusAgendamento.PAGO);

        return agendamentoRepository.save(agendamento);
    }

    @Transactional
    public AgendamentoModel adiarAgendamento(UUID agendamentoId) {

        AgendamentoModel agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Agendamento não encontrado!")
                );

        // Só pagamento pode ser adiado
        if (agendamento.getTipo() != TipoAgendamento.PAGAMENTO) {
            throw new IllegalStateException(
                    "Somente agendamentos de pagamento podem ser adiados!"
            );
        }

        // Só pagamento pendente pode ser adiado
        if (agendamento.getStatus() != StatusAgendamento.PENDENTE) {
            throw new IllegalStateException(
                    "Somente pagamentos pendentes podem ser adiados!"
            );
        }

        CicloModel ciclo = agendamento.getCiclo();

        LocalDate dataOriginal = agendamento.getData();

        /*
         * Próxima sexta-feira.
         */
        LocalDate novaData = dataOriginal.plusDays(1);

        while (novaData.getDayOfWeek() != DayOfWeek.FRIDAY) {
            novaData = novaData.plusDays(1);
        }

        /*
         * Busca TODOS os pagamentos a partir da próxima sexta,
         * do mais distante para o mais próximo.
         *
         * Exemplo:
         *
         * 25/09 André
         * 18/09 Marcos
         * 11/09 Lucas
         * 04/09 Pedro
         */
        List<AgendamentoModel> pagamentos = agendamentoRepository
                .findByCicloIdAndTipoAndDataGreaterThanEqualOrderByDataDesc(
                        ciclo.getId(),
                        TipoAgendamento.PAGAMENTO,
                        novaData
                );

        /*
         * Desloca cada pagamento uma sexta-feira para frente.
         *
         * Fazemos do mais distante para o mais próximo
         * para evitar sobrescrever a data de outro pagamento.
         */
        for (AgendamentoModel pagamento : pagamentos) {

            LocalDate novaDataPagamento =
                    pagamento.getData().plusDays(7);

            pagamento.setData(novaDataPagamento);
        }

        /*
         * Salva os pagamentos deslocados.
         */
        agendamentoRepository.saveAll(pagamentos);

        /*
         * O agendamento original permanece no banco
         * como histórico.
         */
        agendamento.setStatus(StatusAgendamento.ADIADO);

        agendamentoRepository.save(agendamento);

        /*
         * Cria o novo pagamento para o mesmo participante
         * na próxima sexta-feira.
         */
        AgendamentoModel novoAgendamento = new AgendamentoModel();

        novoAgendamento.setData(novaData);
        novoAgendamento.setTipo(TipoAgendamento.PAGAMENTO);
        novoAgendamento.setStatus(StatusAgendamento.PENDENTE);
        novoAgendamento.setCiclo(ciclo);
        novoAgendamento.setParticipante(agendamento.getParticipante());

        /*
         * Se a nova data ultrapassar o fim do ciclo,
         * neste momento não alteramos o dataFim automaticamente.
         *
         * Essa regra será tratada separadamente.
         */

        return agendamentoRepository.save(novoAgendamento);
    }
}

