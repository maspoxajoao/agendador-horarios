package com.maspoxajoao.agendador_horarios.controller;

import com.maspoxajoao.agendador_horarios.infrastructure.entity.AgendamentoEntity;
import com.maspoxajoao.agendador_horarios.services.AgendamentoService;
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
    public ResponseEntity<AgendamentoEntity> salvar(@RequestBody AgendamentoEntity agendamento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoService.salvarAgendamento(agendamento));
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoEntity>> buscarDoDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(agendamentoService.buscarAgendamentosDia(data));
    }
    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoEntity> alterarAgendamentos(@PathVariable Long id, @RequestBody AgendamentoEntity agendamento) {
        // Chamando o service de verdade
        return ResponseEntity.ok(agendamentoService.alterarAgendamento(id, agendamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable Long id) {
        agendamentoService.deletarAgendamento(id);
        return ResponseEntity.noContent().build();
    }
}