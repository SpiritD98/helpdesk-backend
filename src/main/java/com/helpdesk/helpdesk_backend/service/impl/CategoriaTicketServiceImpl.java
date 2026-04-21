package com.helpdesk.helpdesk_backend.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.helpdesk.helpdesk_backend.model.CategoriaTicket;
import com.helpdesk.helpdesk_backend.repository.CategoriaTicketRepository;
import com.helpdesk.helpdesk_backend.service.CategoriaTicketService;


@Service
@Transactional
public class CategoriaTicketServiceImpl implements CategoriaTicketService {

    private final CategoriaTicketRepository categoriaTicketRepository;

    public CategoriaTicketServiceImpl(CategoriaTicketRepository categoriaTicketRepository) {
        this.categoriaTicketRepository = categoriaTicketRepository;
    }

    
    @Override
    public CategoriaTicket guardar(CategoriaTicket categoriaTicket) {
        if (categoriaTicketRepository.existsByNombreAndEmpresaId(categoriaTicket.getNombre(), categoriaTicket.getEmpresa().getId())) {
            throw new IllegalArgumentException("Ya existe una categoría de ticket con el mismo nombre para esta empresa");
        }
        return categoriaTicketRepository.save(categoriaTicket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaTicket> listarTodas() {
        return categoriaTicketRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoriaTicket> buscarPorId(Long id) {
        return categoriaTicketRepository.findById(id);
    }

    @Override
    public CategoriaTicket actualizar(Long id, CategoriaTicket categoriaTicket) {
        CategoriaTicket categoriaExistente = categoriaTicketRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Error: Categoría de ticket no encontrada con id: " + id));

        if(!categoriaExistente.getNombre().equals(categoriaTicket.getNombre()) 
            && categoriaTicketRepository.existsByNombreAndEmpresaId(categoriaTicket.getNombre(), categoriaTicket.getEmpresa().getId())) {
            throw new RuntimeException("Ya existe una categoría de ticket con el mismo nombre para esta empresa");
        }

        categoriaExistente.setNombre(categoriaTicket.getNombre());
        categoriaExistente.setDescripcion(categoriaTicket.getDescripcion());
        categoriaExistente.setActivo(categoriaTicket.isActivo());
        categoriaExistente.setEmpresa(categoriaTicket.getEmpresa());

        return categoriaTicketRepository.save(categoriaExistente);
    }

    @Override
    public void eliminar(Long id) {
        if (!categoriaTicketRepository.existsById(id)) {
            throw new RuntimeException("Error: No se puede eliminar. Categoría de ticket no encontrada con id: " + id);
        }
        categoriaTicketRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaTicket> listarPorActiva(boolean activa) {
        return categoriaTicketRepository.findByActiva(activa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaTicket> listarPorEmpresaId(Long empresaId) {
        return categoriaTicketRepository.findByEmpresaId(empresaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaTicket> listarPorEmpresaIdYActiva(Long empresaId, boolean activa) {
        return categoriaTicketRepository.findByEmpresaIdAndActiva(empresaId, activa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoriaTicket> buscarPorNombreYEmpresaId(String nombre, Long empresaId) {
        return categoriaTicketRepository.findByNombreAndEmpresaId(nombre, empresaId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombreYEmpresaId(String nombre, Long empresaId) {
        return categoriaTicketRepository.existsByNombreAndEmpresaId(nombre, empresaId);
    }

}
