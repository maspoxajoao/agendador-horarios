package com.maspoxajoao.agendador_horarios.dto;

import com.maspoxajoao.agendador_horarios.infrastructure.entity.AgendamentoEntity;

import java.time.LocalDateTime;

public record AgendamentoResponse(
        Long id,
        String servico,
        String profissional,
        LocalDateTime dataHoraAgendamento,
        Integer duracaoMinutos,
        String cliente,
        String telefoneCliente) {

    public static AgendamentoResponse de(AgendamentoEntity e) {
        return new AgendamentoResponse(e.getId(), e.getServico(), e.getProfissional(),
                e.getDataHoraAgendamento(), e.getDuracaoMinutos(), e.getCliente(), e.getTelefoneCliente());
    }
}
