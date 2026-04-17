package com.helpdesk.helpdesk_backend.service;

import java.util.List;
import java.util.Optional;

import com.helpdesk.helpdesk_backend.model.Ticket;
import com.helpdesk.helpdesk_backend.model.enums.EstadoTicket;
import com.helpdesk.helpdesk_backend.model.enums.PrioridadTicket;

public interface TicketService {
    List<Ticket> listarTodos();
    Optional<Ticket> buscarPorId(Long id);
    Ticket guardar(Ticket ticket);
    Ticket actualizar(Long id, Ticket ticket);
    void eliminar(Long id);

    Optional<Ticket> buscarPorCodigo(String codigo);
    List<Ticket> listarPorEmpresaId(Long empresaId);
    List<Ticket> listarPorClienteId(Long clienteId);
    List<Ticket> listarPorAgenteAsignadoId(Long agenteAsignadoId);
    List<Ticket> listarPorCategoriaId(Long categoriaId);
    List<Ticket> listarPorEstado(EstadoTicket estado);
    List<Ticket> listarPorPrioridad(PrioridadTicket prioridad);
    List<Ticket> listarPorEmpresaIdYEstado(Long empresaId, EstadoTicket estado);
    List<Ticket> listarPorEmpresaIdYPrioridad(Long empresaId, PrioridadTicket prioridad);
    boolean existePorCodigo(String codigo);
}
