package com.helpdesk.helpdesk_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.helpdesk.helpdesk_backend.model.Ticket;
import com.helpdesk.helpdesk_backend.model.enums.EstadoTicket;
import com.helpdesk.helpdesk_backend.model.enums.PrioridadTicket;

public interface TicketRepository extends JpaRepository<Ticket, Long>  {

    // TODO: Buscar un ticket por su código.
    // Será útil para mostrar, consultar o ubicar tickets rápidamente.
    Optional<Ticket> findByCodigo(String codigo);

    // TODO: Listar todos los tickets de una empresa.
    // Permite separar la información por empresa en el sistema SaaS.
    List<Ticket> findByEmpresaId(Long empresaId);

    // TODO: Listar tickets creados por un cliente específico.
    // Se usará cuando un usuario quiera ver sus propios tickets.
    List<Ticket> findByClienteId(Long clienteId);

    // TODO: Listar tickets asignados a un agente específico.
    // Muy útil para que cada agente vea lo que tiene pendiente.
    List<Ticket> findByAgenteAsignadoId(Long agenteAsignadoId);

    // TODO: Listar tickets por categoría.
    // Ayuda en filtros, reportes y organización.
    List<Ticket> findByCategoriaId(Long categoriaId);

    // TODO: Listar tickets por estado.
    // Ejemplo: ABIERTO, EN_PROCESO, RESUELTO, CERRADO.
    List<Ticket> findByEstado(EstadoTicket estado);

    // TODO: Listar tickets por prioridad.
    // Ejemplo: BAJA, MEDIA, ALTA, CRITICA.
    List<Ticket> findByPrioridad(PrioridadTicket prioridad);

    // TODO: Listar tickets de una empresa filtrados por estado.
    // Ejemplo: tickets abiertos de una empresa específica.
    List<Ticket> findByEmpresaIdAndEstado(Long empresaId, EstadoTicket estado);

    // TODO: Listar tickets de una empresa filtrados por prioridad.
    // Ejemplo: tickets críticos de una empresa específica.
    List<Ticket> findByEmpresaIdAndPrioridad(Long empresaId, PrioridadTicket prioridad);

    // TODO: Verificar si ya existe un ticket con ese código.
    // Servirá para evitar duplicados.
    boolean existsByCodigo(String codigo);

}