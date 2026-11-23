package com.maspoxajoao.agendador_horarios.infrastructure.repository;

import com.maspoxajoao.agendador_horarios.infrastructure.entity.AgendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<AgendamentoEntity, Long> {

    // Agendamentos de um intervalo [inicio, fim): inclui o inicio e exclui o fim
    List<AgendamentoEntity> findByDataHoraAgendamentoGreaterThanEqualAndDataHoraAgendamentoLessThan(
            LocalDateTime inicio, LocalDateTime fim);

    // Existe agendamento do profissional estritamente entre depoisDe e antesDe?
    boolean existsByProfissionalAndDataHoraAgendamentoGreaterThanAndDataHoraAgendamentoLessThan(
            String profissional, LocalDateTime depoisDe, LocalDateTime antesDe);

    // Mesma consulta, ignorando um id (usada na alteracao para nao conflitar consigo mesmo)
    boolean existsByProfissionalAndDataHoraAgendamentoGreaterThanAndDataHoraAgendamentoLessThanAndIdNot(
            String profissional, LocalDateTime depoisDe, LocalDateTime antesDe, Long id);
}
