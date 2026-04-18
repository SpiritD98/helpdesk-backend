package com.helpdesk.helpdesk_backend.controller;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.helpdesk.helpdesk_backend.model.CategoriaTicket;
import com.helpdesk.helpdesk_backend.service.CategoriaTicketService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaTicketRestController {

    private final CategoriaTicketService categoriaService;
    //Método crear
    @PostMapping
    public ResponseEntity<CategoriaTicket> crear(@RequestBody CategoriaTicket categoria) {
        return ResponseEntity.ok(categoriaService.crear(categoria));
    }
    //Método listar
    @GetMapping
    public ResponseEntity<List<CategoriaTicket>> listarTodas() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }
    //Método obtener por id
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaTicket> obtenerPorId(@PathVariable Long id) {
        return categoriaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    //Método actualizar
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaTicket> actualizar(
            @PathVariable Long id,
            @RequestBody CategoriaTicket categoria) {

        return ResponseEntity.ok(categoriaService.actualizar(id, categoria));
    }

    // Método de la eliminación lógica
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }

    // FILTROS 
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<CategoriaTicket>> porEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(categoriaService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/activas/{activa}")
    public ResponseEntity<List<CategoriaTicket>> porEstado(@PathVariable boolean activa) {
        return ResponseEntity.ok(categoriaService.listarActivas(activa));
    }

    @GetMapping("/empresa/{empresaId}/activas/{activa}")
    public ResponseEntity<List<CategoriaTicket>> porEmpresaYEstado(
            @PathVariable Long empresaId,
            @PathVariable boolean activa) {
        return ResponseEntity.ok(categoriaService.listarPorEmpresaYActiva(empresaId, activa));
    }
}