package com.helpdesk.helpdesk_backend.service.serviceImpl;

import java.util.List;
import java.util.Optional;


import com.helpdesk.helpdesk_backend.model.ProblemaTicket;
import com.helpdesk.helpdesk_backend.repository.ProblemaTicketRepository;
import com.helpdesk.helpdesk_backend.service.ProblemaTicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProblemaTicketServiceImpl implements ProblemaTicketService{

    private final ProblemaTicketRepository problemaTicketRepository;

    public ProblemaTicketServiceImpl(ProblemaTicketRepository problemaTicketRepository) {
        this.problemaTicketRepository = problemaTicketRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemaTicket> listarTodos() {
        return problemaTicketRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProblemaTicket> buscarPorId(Long id) {
        return problemaTicketRepository.findById(id);
    }

    @Override
    public ProblemaTicket guardar(ProblemaTicket problemaTicket) {
        return problemaTicketRepository.save(problemaTicket);
    }

    @Override
    public ProblemaTicket actualizar(Long id, ProblemaTicket problemaTicket) {
        ProblemaTicket existente = problemaTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Problema no encontrado con el id " + id));

        existente.setNombre(problemaTicket.getNombre());
        existente.setDescripcion(problemaTicket.getDescripcion());
        existente.setActivo(problemaTicket.isActivo());
        existente.setCategoria(problemaTicket.getCategoria());

        return problemaTicketRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!problemaTicketRepository.existsById(id)) {
            throw new RuntimeException("Error: No se puede eliminar. Problema no encontrado con id " + id);
        }
        problemaTicketRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemaTicket> listarPorCategoriaId(Long categoriaId) {
        return problemaTicketRepository.findByCategoriaId(categoriaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemaTicket> listarPorActivo(boolean activo) {
        return problemaTicketRepository.findByActivo(activo);
    }

}
