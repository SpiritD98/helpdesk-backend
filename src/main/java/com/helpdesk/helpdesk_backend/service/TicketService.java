package com.helpdesk.helpdesk_backend.service;
import java.util.List;
import java.util.Optional;
import com.helpdesk.helpdesk_backend.model.Ticket;
import com.helpdesk.helpdesk_backend.model.enums.EstadoTicket;
import com.helpdesk.helpdesk_backend.model.enums.PrioridadTicket;

public interface TicketService {
    
    // Busca todos los tickets, sin filtros
    List<Ticket> listarTodos();

    // Busca ticket por id, usa optional para evitar errores de inexistencia
    Optional<Ticket> buscarPorId(Long id);

    // Crear nuevo ticket
    Ticket guardar(Ticket ticket);

    // Actualizar ticket existente
    Ticket actualizar(Long id, Ticket ticket);

    // Desactivar ticket (NO ELIMINA)
    void eliminar(Long id);

    // Filtros
    // Busca ticket por código, usa optional para evitar errores de inexistencia
    Optional<Ticket> buscarPorCodigo(String codigo);

    // Lista tickets por empresa, cliente, agente asignado, categoría, estado o prioridad
    List<Ticket> listarPorEmpresaId(Long empresaId);

    List<Ticket> listarPorClienteId(Long clienteId);

    List<Ticket> listarPorAgenteAsignadoId(Long agenteAsignadoId);

    List<Ticket> listarPorCategoriaId(Long categoriaId);

    List<Ticket> listarPorEstado(EstadoTicket estado);

    List<Ticket> listarPorPrioridad(PrioridadTicket prioridad);

    // Lista tickets por empresa y estado o prioridad
    List<Ticket> listarPorEmpresaIdYEstado(Long empresaId, EstadoTicket estado);

    List<Ticket> listarPorEmpresaIdYPrioridad(Long empresaId, PrioridadTicket prioridad);
    
    // Verifica si existe ticket por código, para evitar duplicados
    boolean existePorCodigo(String codigo);
}
