package com.helpdesk.helpdesk_backend.service.impl;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.helpdesk.helpdesk_backend.model.Ticket;
import com.helpdesk.helpdesk_backend.model.enums.EstadoTicket;
import com.helpdesk.helpdesk_backend.model.enums.PrioridadTicket;
import com.helpdesk.helpdesk_backend.repository.TicketRepository;
import com.helpdesk.helpdesk_backend.service.TicketService;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final com.helpdesk.helpdesk_backend.repository.UsuarioRepository usuarioRepository;
    private final com.helpdesk.helpdesk_backend.repository.EmpresaRepository empresaRepository;
    private final com.helpdesk.helpdesk_backend.repository.CategoriaTicketRepository categoriaRepository;

    public TicketServiceImpl(TicketRepository ticketRepository,
                             com.helpdesk.helpdesk_backend.repository.UsuarioRepository usuarioRepository,
                             com.helpdesk.helpdesk_backend.repository.EmpresaRepository empresaRepository,
                             com.helpdesk.helpdesk_backend.repository.CategoriaTicketRepository categoriaRepository) {
        this.ticketRepository = ticketRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> listarTodos() {
        return ticketRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> buscarPorId(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public Ticket guardar(Ticket ticket) {
        // Al recibir los objetos anidados con solo el ID desde el JSON, 
        // son entidades "desconectadas". Debemos obtener las referencias reales 
        // para que Hibernate no de error al guardar.
        if (ticket.getCliente() != null && ticket.getCliente().getId() != null) {
            ticket.setCliente(usuarioRepository.getReferenceById(ticket.getCliente().getId()));
        }
        if (ticket.getEmpresa() != null && ticket.getEmpresa().getId() != null) {
            ticket.setEmpresa(empresaRepository.getReferenceById(ticket.getEmpresa().getId()));
        }
        if (ticket.getCategoria() != null && ticket.getCategoria().getId() != null) {
            ticket.setCategoria(categoriaRepository.getReferenceById(ticket.getCategoria().getId()));
        }
        if (ticket.getAgenteAsignado() != null && ticket.getAgenteAsignado().getId() != null) {
            ticket.setAgenteAsignado(usuarioRepository.getReferenceById(ticket.getAgenteAsignado().getId()));
        }

        // Generar un código único si no lo tiene
        if (ticket.getCodigo() == null || ticket.getCodigo().isEmpty()) {
            ticket.setCodigo("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        if (ticketRepository.existsByCodigo(ticket.getCodigo())) {
            throw new RuntimeException("Error: Ya existe un ticket con ese código.");
        }
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket actualizar(Long id, Ticket ticket) {
        Ticket ticketExistente = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Ticket no encontrado con el id " + id));

        ticketExistente.setTitulo(ticket.getTitulo());
        ticketExistente.setDescripcion(ticket.getDescripcion());
        if (ticket.getEstado() != null) {
            ticketExistente.setEstado(ticket.getEstado());
        }
        if (ticket.getPrioridad() != null) {
            ticketExistente.setPrioridad(ticket.getPrioridad());
        }
        ticketExistente.setCliente(ticket.getCliente());
        ticketExistente.setAgenteAsignado(ticket.getAgenteAsignado());
        ticketExistente.setCategoria(ticket.getCategoria());
        ticketExistente.setEmpresa(ticket.getEmpresa());

        return ticketRepository.save(ticketExistente);
    }

    @Override
    public void eliminar(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Error: No se puede eliminar. Ticket no encontrado con el id " + id);
        }
        ticketRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> buscarPorCodigo(String codigo) {
        return ticketRepository.findByCodigo(codigo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> listarPorEmpresaId(Long empresaId) {
        return ticketRepository.findByEmpresaId(empresaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> listarPorClienteId(Long clienteId) {
        return ticketRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> listarPorAgenteAsignadoId(Long agenteAsignadoId) {
        return ticketRepository.findByAgenteAsignadoId(agenteAsignadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> listarPorCategoriaId(Long categoriaId) {
        return ticketRepository.findByCategoriaId(categoriaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> listarPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> listarPorPrioridad(PrioridadTicket prioridad) {
        return ticketRepository.findByPrioridad(prioridad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> listarPorEmpresaIdYEstado(Long empresaId, EstadoTicket estado) {
        return ticketRepository.findByEmpresaIdAndEstado(empresaId, estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> listarPorEmpresaIdYPrioridad(Long empresaId, PrioridadTicket prioridad) {
        return ticketRepository.findByEmpresaIdAndPrioridad(empresaId, prioridad);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCodigo(String codigo) {
        return ticketRepository.existsByCodigo(codigo);
    }
}
