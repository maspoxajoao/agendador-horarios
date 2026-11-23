package com.maspoxajoao.agendador_horarios.exception;

public class AgendamentoNaoEncontradoException extends RuntimeException {

    public AgendamentoNaoEncontradoException(Long id) {
        super("Agendamento nao encontrado com o ID: " + id);
    }
}
