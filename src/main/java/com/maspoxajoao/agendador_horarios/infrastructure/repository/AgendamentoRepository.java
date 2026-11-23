package com.maspoxajoao.agendador_horarios.infrastructure.repository;

import com.maspoxajoao.agendador_horarios.infrastructure.entity.AgendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<AgendamentoEntity, Long> {

    // Agendamentos de um intervalo [inicio, fim): inclui o inicio e exclui o fim
    List<AgendamentoEntity> findByDataHoraAgendamentoGreaterThanEqualAndDataHoraAgendamentoLessThan(
            LocalDateTime inicio, LocalDateTime fim);

    // Agendamentos do profissional que comecam estritamente entre depoisDe e antesDe.
    // O service filtra os que de fato se sobrepoem, considerando a duracao de cada um.
    List<AgendamentoEntity> findByProfissionalAndDataHoraAgendamentoGreaterThanAndDataHoraAgendamentoLessThan(
            String profissional, LocalDateTime depoisDe, LocalDateTime antesDe);
}
