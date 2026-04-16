package com.helpdesk.helpdesk_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.helpdesk.helpdesk_backend.model.CategoriaTicket;
import com.helpdesk.helpdesk_backend.service.CategoriaTicketService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaTicketController {

    private final CategoriaTicketService categoriaTicketService;

    public CategoriaTicketController(CategoriaTicketService categoriaTicketService) {
        this.categoriaTicketService = categoriaTicketService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaTicket>> listarTodos() {
        return ResponseEntity.ok(categoriaTicketService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaTicket> buscarPorId(@PathVariable Long id) {
        return categoriaTicketService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<CategoriaTicket>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(categoriaTicketService.listarPorEmpresaId(empresaId));
    }

    @PostMapping
    public ResponseEntity<CategoriaTicket> guardar(@RequestBody CategoriaTicket categoriaTicket) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaTicketService.guardar(categoriaTicket));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaTicket> actualizar(@PathVariable Long id, @RequestBody CategoriaTicket categoriaTicket) {
        return ResponseEntity.ok(categoriaTicketService.actualizar(id, categoriaTicket));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaTicketService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
