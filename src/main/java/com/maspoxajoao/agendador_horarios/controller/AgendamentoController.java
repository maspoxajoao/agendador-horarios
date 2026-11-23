package com.maspoxajoao.agendador_horarios.controller;

import com.maspoxajoao.agendador_horarios.dto.AgendamentoRequest;
import com.maspoxajoao.agendador_horarios.dto.AgendamentoResponse;
import com.maspoxajoao.agendador_horarios.services.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<AgendamentoResponse> salvar(@Valid @RequestBody AgendamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AgendamentoResponse.de(agendamentoService.salvarAgendamento(request)));
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoResponse>> buscarDoDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(agendamentoService.buscarAgendamentosDia(data).stream()
                .map(AgendamentoResponse::de)
                .toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoResponse> alterar(@PathVariable Long id,
                                                       @Valid @RequestBody AgendamentoRequest request) {
        return ResponseEntity.ok(AgendamentoResponse.de(agendamentoService.alterarAgendamento(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        agendamentoService.deletarAgendamento(id);
        return ResponseEntity.noContent().build();
    }
}
