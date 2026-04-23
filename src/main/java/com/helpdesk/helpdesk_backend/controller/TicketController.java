package com.helpdesk.helpdesk_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.helpdesk.helpdesk_backend.model.Ticket;
import com.helpdesk.helpdesk_backend.service.TicketService;


@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> listarTodos() {
        return ResponseEntity.ok(ticketService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> buscarPorId(@PathVariable Long id) {
        return ticketService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Ticket> buscarPorCodigo(@PathVariable String codigo) {
        return ticketService.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Ticket>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(ticketService.listarPorEmpresaId(empresaId));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Ticket>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ticketService.listarPorClienteId(clienteId));
    }

    @GetMapping("/agente/{agenteId}")
    public ResponseEntity<List<Ticket>> listarPorAgente(@PathVariable Long agenteId) {
        return ResponseEntity.ok(ticketService.listarPorAgenteAsignadoId(agenteId));
    }

    @PostMapping
    public ResponseEntity<Ticket> guardar(@RequestBody Ticket ticket) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.guardar(ticket));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> actualizar(@PathVariable Long id, @RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.actualizar(id, ticket));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Ticket> actualizarEstado(@PathVariable Long id, @RequestBody java.util.Map<String, String> updates) {
        // Obtenemos el nuevo estado del JSON enviado en Postman: {"estado": "RESUELTO"}
        String nuevoEstadoStr = updates.get("estado");
        if (nuevoEstadoStr == null) {
            return ResponseEntity.badRequest().build();
        }
        // Convertimos el texto ("RESUELTO") al tipo Enum (EstadoTicket.RESUELTO)
        com.helpdesk.helpdesk_backend.model.enums.EstadoTicket estado = com.helpdesk.helpdesk_backend.model.enums.EstadoTicket.valueOf(nuevoEstadoStr.toUpperCase());
        
        // Llamamos al servicio que creamos en el paso anterior
        return ResponseEntity.ok(ticketService.actualizarEstado(id, estado));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ticketService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
