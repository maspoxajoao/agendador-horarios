package com.maspoxajoao.agendador_horarios.services;

import com.maspoxajoao.agendador_horarios.dto.AgendamentoRequest;
import com.maspoxajoao.agendador_horarios.exception.AgendamentoNaoEncontradoException;
import com.maspoxajoao.agendador_horarios.exception.HorarioIndisponivelException;
import com.maspoxajoao.agendador_horarios.infrastructure.entity.AgendamentoEntity;
import com.maspoxajoao.agendador_horarios.infrastructure.repository.AgendamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    private static final LocalDateTime DEZ_HORAS = LocalDateTime.of(2026, 12, 1, 10, 0);

    @Mock
    private AgendamentoRepository repository;

    @InjectMocks
    private AgendamentoService service;

    private AgendamentoRequest pedido(LocalDateTime horario, Integer duracao) {
        return new AgendamentoRequest("Corte", "Ana", horario, duracao, "Maria", "31999999999");
    }

    private AgendamentoRequest pedido() {
        return pedido(DEZ_HORAS, 60);
    }

    private AgendamentoEntity existente(Long id, LocalDateTime horario, int duracao) {
        AgendamentoEntity e = new AgendamentoEntity();
        e.setId(id);
        e.setProfissional("Ana");
        e.setDataHoraAgendamento(horario);
        e.setDuracaoMinutos(duracao);
        return e;
    }

    private void profissionalTem(AgendamentoEntity... agendamentos) {
        when(repository.findByProfissionalAndDataHoraAgendamentoGreaterThanAndDataHoraAgendamentoLessThan(
                any(), any(), any())).thenReturn(List.of(agendamentos));
    }

    @Test
    void salvaQuandoProfissionalEstaLivre() {
        profissionalTem();
        when(repository.save(any(AgendamentoEntity.class))).thenAnswer(i -> i.getArgument(0));

        AgendamentoEntity salvo = service.salvarAgendamento(pedido());

        assertThat(salvo.getProfissional()).isEqualTo("Ana");
        assertThat(salvo.getDataHoraAgendamento()).isEqualTo(DEZ_HORAS);
        assertThat(salvo.getDuracaoMinutos()).isEqualTo(60);
    }

    @Test
    void duracaoOmitidaUsaUmaHora() {
        profissionalTem();
        when(repository.save(any(AgendamentoEntity.class))).thenAnswer(i -> i.getArgument(0));

        AgendamentoEntity salvo = service.salvarAgendamento(pedido(DEZ_HORAS, null));

        assertThat(salvo.getDuracaoMinutos()).isEqualTo(60);
    }

    @Test
    void recusaQuandoNovoComecaDentroDeAtendimentoExistente() {
        // existente 09:30-10:30; novo 10:00 conflita
        profissionalTem(existente(1L, DEZ_HORAS.minusMinutes(30), 60));

        assertThatThrownBy(() -> service.salvarAgendamento(pedido()))
                .isInstanceOf(HorarioIndisponivelException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void recusaQuandoNovoInvadeOInicioDeOutro() {
        // novo 10:00-11:00; existente 10:30-11:00 conflita
        profissionalTem(existente(1L, DEZ_HORAS.plusMinutes(30), 30));

        assertThatThrownBy(() -> service.salvarAgendamento(pedido()))
                .isInstanceOf(HorarioIndisponivelException.class);
    }

    @Test
    void atendimentosColadosDeMeiaHoraNaoConflitam() {
        // existente 09:30-10:00; novo 10:00-10:30
        profissionalTem(existente(1L, DEZ_HORAS.minusMinutes(30), 30));
        when(repository.save(any(AgendamentoEntity.class))).thenAnswer(i -> i.getArgument(0));

        AgendamentoEntity salvo = service.salvarAgendamento(pedido(DEZ_HORAS, 30));

        assertThat(salvo.getDuracaoMinutos()).isEqualTo(30);
    }

    @Test
    void alterarIgnoraOProprioAgendamentoNaChecagemDeConflito() {
        AgendamentoEntity atual = existente(7L, DEZ_HORAS, 60);
        when(repository.findById(7L)).thenReturn(Optional.of(atual));
        profissionalTem(atual);
        when(repository.save(atual)).thenReturn(atual);

        AgendamentoEntity alterado = service.alterarAgendamento(7L, pedido(DEZ_HORAS, 30));

        assertThat(alterado.getCliente()).isEqualTo("Maria");
        assertThat(alterado.getTelefoneCliente()).isEqualTo("31999999999");
        assertThat(alterado.getDuracaoMinutos()).isEqualTo(30);
    }

    @Test
    void alterarIdInexistenteLancaNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.alterarAgendamento(99L, pedido()))
                .isInstanceOf(AgendamentoNaoEncontradoException.class);
    }

    @Test
    void deletarIdInexistenteLancaNaoEncontrado() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deletarAgendamento(99L))
                .isInstanceOf(AgendamentoNaoEncontradoException.class);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void buscaDoDiaUsaIntervaloDeMeiaNoiteAteMeiaNoiteDoDiaSeguinte() {
        LocalDate dia = LocalDate.of(2026, 12, 1);

        service.buscarAgendamentosDia(dia);

        verify(repository).findByDataHoraAgendamentoGreaterThanEqualAndDataHoraAgendamentoLessThan(
                dia.atStartOfDay(), dia.plusDays(1).atStartOfDay());
    }
}
