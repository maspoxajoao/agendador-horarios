package com.maspoxajoao.agendador_horarios.exception;

import java.time.LocalDateTime;

public class HorarioIndisponivelException extends RuntimeException {

    public HorarioIndisponivelException(String profissional, LocalDateTime horario) {
        super("Horario indisponivel para " + profissional + " em " + horario);
    }
}
