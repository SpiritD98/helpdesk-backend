package com.helpdesk.helpdesk_backend.service.serviceImpl;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.helpdesk.helpdesk_backend.model.Ticket;
import com.helpdesk.helpdesk_backend.model.enums.EstadoTicket;
import com.helpdesk.helpdesk_backend.model.enums.PrioridadTicket;
import com.helpdesk.helpdesk_backend.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import com.helpdesk.helpdesk_backend.service.TicketService;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    //Conecta a la base de datos
    private final TicketRepository ticketRepository;

    //Crea un ticket
    @Override
    public Ticket crearTicket(Ticket ticket) {
        // Validación de código único
        if (ticketRepository.existsByCodigo(ticket.getCodigo())) {
            throw new RuntimeException("Ya existe un ticket con ese código");
        }
        return ticketRepository.save(ticket);
    }
    //Lista todos los tickets
    @Override
    public List<Ticket> listarTodos() {
        return ticketRepository.findAll();
    }
    //Busca por id
    @Override
    public Optional<Ticket> obtenerPorId(Long id) {
        return ticketRepository.findById(id);
    }
    //Busca por código
    @Override
    public Optional<Ticket> obtenerPorCodigo(String codigo) {
        return ticketRepository.findByCodigo(codigo);
    }
    //Actualiza un ticket
    @Override
    public Ticket actualizarTicket(Long id, Ticket ticket) {
        Ticket existente = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        existente.setTitulo(ticket.getTitulo());
        existente.setDescripcion(ticket.getDescripcion());
        existente.setEstado(ticket.getEstado());
        existente.setPrioridad(ticket.getPrioridad());
        existente.setCategoria(ticket.getCategoria());
        existente.setAgenteAsignado(ticket.getAgenteAsignado());
        //Guarda el cambio
        return ticketRepository.save(existente);
    }
    //Elimina un ticket
    @Override
    public void eliminarTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    //Filtros
    @Override
    public List<Ticket> listarPorEmpresa(Long empresaId) {
        return ticketRepository.findByEmpresaId(empresaId);
    }

    @Override
    public List<Ticket> listarPorCliente(Long clienteId) {
        return ticketRepository.findByClienteId(clienteId);
    }

    @Override
    public List<Ticket> listarPorAgente(Long agenteId) {
        return ticketRepository.findByAgenteAsignadoId(agenteId);
    }

    @Override
    public List<Ticket> listarPorCategoria(Long categoriaId) {
        return ticketRepository.findByCategoriaId(categoriaId);
    }

    @Override
    public List<Ticket> listarPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado);
    }

    @Override
    public List<Ticket> listarPorPrioridad(PrioridadTicket prioridad) {
        return ticketRepository.findByPrioridad(prioridad);
    }
}