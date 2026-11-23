package com.maspoxajoao.agendador_horarios.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AgendamentoRequest(
        @NotBlank String servico,
        @NotBlank String profissional,
        @NotNull @Future LocalDateTime dataHoraAgendamento,
        Integer duracaoMinutos, // opcional: 30 ou 60 (padrao 60)
        @NotBlank String cliente,
        String telefoneCliente) {

    @AssertTrue(message = "duracaoMinutos deve ser 30 ou 60")
    public boolean isDuracaoValida() {
        return duracaoMinutos == null || duracaoMinutos == 30 || duracaoMinutos == 60;
    }

    public int duracaoOuPadrao() {
        return duracaoMinutos == null ? 60 : duracaoMinutos;
    }
}
