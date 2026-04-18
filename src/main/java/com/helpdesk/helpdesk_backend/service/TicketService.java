package com.helpdesk.helpdesk_backend.service;
import java.util.List;
import java.util.Optional;
import com.helpdesk.helpdesk_backend.model.Ticket;
import com.helpdesk.helpdesk_backend.model.enums.EstadoTicket;
import com.helpdesk.helpdesk_backend.model.enums.PrioridadTicket;

public interface TicketService {
    //Método para crea un ticket nuevo
    Ticket crearTicket(Ticket ticket);
    //Devuelve los tickets de la bd
    List<Ticket> listarTodos();
    //Busca ticket por id, usa optional para evitar errores de inexistencia
    Optional<Ticket> obtenerPorId(Long id);
    //Busca ticket por codigo, usa optional para evitar errores de inexistencia
    Optional<Ticket> obtenerPorCodigo(String codigo);
    //Actualiza ticket existente
    Ticket actualizarTicket(Long id, Ticket ticket);
    //Elimina ticket por id
    void eliminarTicket(Long id);

    // Filtros

    List<Ticket> listarPorEmpresa(Long empresaId); //Por empresa
    List<Ticket> listarPorCliente(Long clienteId); //Por usuario
    List<Ticket> listarPorAgente(Long agenteId); //Por agente de soporte
    List<Ticket> listarPorCategoria(Long categoriaId); //Por categoria
    List<Ticket> listarPorEstado(EstadoTicket estado); //Por estado
    List<Ticket> listarPorPrioridad(PrioridadTicket prioridad); //Por prioridad
}
