package com.helpdesk.helpdesk_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.helpdesk.helpdesk_backend.model.Ticket;
import com.helpdesk.helpdesk_backend.model.enums.EstadoTicket;
import com.helpdesk.helpdesk_backend.model.enums.PrioridadTicket;
import com.helpdesk.helpdesk_backend.service.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketRestController {

    private final TicketService ticketService;
    //Método crear
    @PostMapping
    public ResponseEntity<Ticket> crear(@RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.crearTicket(ticket));
    }
    //Método listar
    @GetMapping
    public ResponseEntity<List<Ticket>> listarTodos() {
        return ResponseEntity.ok(ticketService.listarTodos());
    }
    //Método obtener por id
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> obtenerPorId(@PathVariable Long id) {
        return ticketService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                //Si no existe devuelve 404
                .orElse(ResponseEntity.notFound().build());
    }
    //Método obtener por codigo
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Ticket> obtenerPorCodigo(@PathVariable String codigo) {
        return ticketService.obtenerPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    //Método actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> actualizar(@PathVariable Long id, @RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.actualizarTicket(id, ticket));
    }
    //Método eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ticketService.eliminarTicket(id);
        return ResponseEntity.noContent().build();
    }

    // FILTROS 

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Ticket>> porEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(ticketService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Ticket>> porEstado(@PathVariable EstadoTicket estado) {
        return ResponseEntity.ok(ticketService.listarPorEstado(estado));
    }

    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<Ticket>> porPrioridad(@PathVariable PrioridadTicket prioridad) {
        return ResponseEntity.ok(ticketService.listarPorPrioridad(prioridad));
    }
}
