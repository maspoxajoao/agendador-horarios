package com.maspoxajoao.agendador_horarios.services;

import com.maspoxajoao.agendador_horarios.infrastructure.entity.AgendamentoEntity;
import com.maspoxajoao.agendador_horarios.infrastructure.repository.AgendamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor //Injeção de dependencia
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;

    //Verifica se o horario esta disponivel e salva o agendamento
    public AgendamentoEntity salvarAgendamento(AgendamentoEntity agendamento) {

        LocalDateTime horaAgendamento = agendamento.getDataHoraAgendamento();
        LocalDateTime horaFim = agendamento.getDataHoraAgendamento().plusHours(1);
        AgendamentoEntity agendados = agendamentoRepository.findByServicoAndDataHoraAgendamentoBetween(agendamento.getServico(), horaAgendamento, horaFim);

        if (Objects.nonNull(agendados)) {
            throw new IllegalStateException("Horario ja esta preenchido");
        }
        return agendamentoRepository.save(agendamento);
    }

    //Deleta o Agendamento pelo horario do agendamento e pelo cliente
    public void deletarAgendamento(Long id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new RuntimeException("Não foi possível deletar: Agendamento não encontrado com o ID: " + id);
        }
        agendamentoRepository.deleteById(id);
    }

    //Busca agendamentos do dia
    public List<AgendamentoEntity> buscarAgendamentosDia(LocalDate data) {
        LocalDateTime primeiraHoraDia = data.atStartOfDay();
        LocalDateTime horaFinal = data.atTime(23, 59, 59);
        return agendamentoRepository.findByDataHoraAgendamentoBetween(primeiraHoraDia, horaFinal);
    }

    //Subsittui o ID pelo novo id, e altera salvando as informações novas
    public AgendamentoEntity alterarAgendamento(Long id, AgendamentoEntity novosDados) {
        AgendamentoEntity agendaExistente = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        // Atualiza os dados
        agendaExistente.setCliente(novosDados.getCliente());
        agendaExistente.setServico(novosDados.getServico());
        agendaExistente.setDataHoraAgendamento(novosDados.getDataHoraAgendamento());

        return agendamentoRepository.save(agendaExistente);
    }

}
