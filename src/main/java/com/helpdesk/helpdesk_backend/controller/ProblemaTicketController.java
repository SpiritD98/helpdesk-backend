package com.helpdesk.helpdesk_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.helpdesk.helpdesk_backend.model.ProblemaTicket;
import com.helpdesk.helpdesk_backend.service.ProblemaTicketService;

@RestController
@RequestMapping("/api/problemas")
public class ProblemaTicketController {

    private final ProblemaTicketService problemaTicketService;

    public ProblemaTicketController(ProblemaTicketService problemaTicketService) {
        this.problemaTicketService = problemaTicketService;
    }

    @GetMapping
    public ResponseEntity<List<ProblemaTicket>> listarTodos() {
        return ResponseEntity.ok(problemaTicketService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemaTicket> buscarPorId(@PathVariable Long id) {
        return problemaTicketService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProblemaTicket>> listarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(problemaTicketService.listarPorCategoriaId(categoriaId));
    }

    @PostMapping
    public ResponseEntity<ProblemaTicket> guardar(@RequestBody ProblemaTicket problemaTicket) {
        return ResponseEntity.status(HttpStatus.CREATED).body(problemaTicketService.guardar(problemaTicket));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProblemaTicket> actualizar(@PathVariable Long id, @RequestBody ProblemaTicket problemaTicket) {
        return ResponseEntity.ok(problemaTicketService.actualizar(id, problemaTicket));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        problemaTicketService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
