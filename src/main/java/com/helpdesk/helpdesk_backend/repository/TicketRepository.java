package com.helpdesk.helpdesk_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.helpdesk.helpdesk_backend.model.Ticket;
import com.helpdesk.helpdesk_backend.model.enums.EstadoTicket;
import com.helpdesk.helpdesk_backend.model.enums.PrioridadTicket;

public interface TicketRepository extends JpaRepository<Ticket, Long>  {
 
    Optional<Ticket> findByCodigo(String codigo);

    List<Ticket> findByEmpresaId(Long empresaId);

    List<Ticket> findByClienteId(Long clienteId);

    List<Ticket> findByAgenteAsignadoId(Long agenteAsignadoId);

    List<Ticket> findByCategoriaId(Long categoriaId);

    List<Ticket> findByProblemaId(Long problemaId);

    List<Ticket> findByEstado(EstadoTicket estado);

    List<Ticket> findByPrioridad(PrioridadTicket prioridad);

    List<Ticket> findByEmpresaIdAndEstado(Long empresaId, EstadoTicket estado);

    List<Ticket> findByEmpresaIdAndPrioridad(Long empresaId, PrioridadTicket prioridad);

    boolean existsByCodigo(String codigo);

}