package com.maspoxajoao.agendador_horarios.services;

import com.maspoxajoao.agendador_horarios.dto.AgendamentoRequest;
import com.maspoxajoao.agendador_horarios.exception.AgendamentoNaoEncontradoException;
import com.maspoxajoao.agendador_horarios.exception.HorarioIndisponivelException;
import com.maspoxajoao.agendador_horarios.infrastructure.entity.AgendamentoEntity;
import com.maspoxajoao.agendador_horarios.infrastructure.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor // Injecao de dependencia por construtor
public class AgendamentoService {

    // Maior duracao possivel: usada para olhar para tras na busca de conflitos
    private static final long DURACAO_MAXIMA_EM_MINUTOS = 60;

    private final AgendamentoRepository agendamentoRepository;

    // Cria o agendamento se o profissional estiver livre no intervalo (inicio + duracao)
    public AgendamentoEntity salvarAgendamento(AgendamentoRequest dados) {
        validarDisponibilidade(dados, null);
        AgendamentoEntity agendamento = new AgendamentoEntity();
        preencher(agendamento, dados);
        return agendamentoRepository.save(agendamento);
    }

    // Remove o agendamento pelo id
    public void deletarAgendamento(Long id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new AgendamentoNaoEncontradoException(id);
        }
        agendamentoRepository.deleteById(id);
    }

    // Lista os agendamentos do dia: de 00:00 do dia ate 00:00 do dia seguinte (exclusivo)
    public List<AgendamentoEntity> buscarAgendamentosDia(LocalDate data) {
        return agendamentoRepository.findByDataHoraAgendamentoGreaterThanEqualAndDataHoraAgendamentoLessThan(
                data.atStartOfDay(), data.plusDays(1).atStartOfDay());
    }

    // Altera o agendamento, ignorando o proprio id na checagem de conflito
    public AgendamentoEntity alterarAgendamento(Long id, AgendamentoRequest dados) {
        AgendamentoEntity existente = agendamentoRepository.findById(id)
                .orElseThrow(() -> new AgendamentoNaoEncontradoException(id));
        validarDisponibilidade(dados, id);
        preencher(existente, dados);
        return agendamentoRepository.save(existente);
    }

    // Dois atendimentos se sobrepoem quando um comeca antes de o outro terminar.
    // Os limites sao exclusivos: 10:00-10:30 e 10:30-11:00 nao conflitam.
    private void validarDisponibilidade(AgendamentoRequest dados, Long idIgnorado) {
        LocalDateTime inicio = dados.dataHoraAgendamento();
        LocalDateTime fim = inicio.plusMinutes(dados.duracaoOuPadrao());

        boolean ocupado = agendamentoRepository
                .findByProfissionalAndDataHoraAgendamentoGreaterThanAndDataHoraAgendamentoLessThan(
                        dados.profissional(), inicio.minusMinutes(DURACAO_MAXIMA_EM_MINUTOS), fim)
                .stream()
                .filter(outro -> idIgnorado == null || !idIgnorado.equals(outro.getId()))
                .anyMatch(outro -> outro.getDataHoraAgendamento()
                        .plusMinutes(outro.getDuracaoMinutos()).isAfter(inicio));

        if (ocupado) {
            throw new HorarioIndisponivelException(dados.profissional(), inicio);
        }
    }

    private void preencher(AgendamentoEntity agendamento, AgendamentoRequest dados) {
        agendamento.setServico(dados.servico());
        agendamento.setProfissional(dados.profissional());
        agendamento.setDataHoraAgendamento(dados.dataHoraAgendamento());
        agendamento.setDuracaoMinutos(dados.duracaoOuPadrao());
        agendamento.setCliente(dados.cliente());
        agendamento.setTelefoneCliente(dados.telefoneCliente());
    }
}
