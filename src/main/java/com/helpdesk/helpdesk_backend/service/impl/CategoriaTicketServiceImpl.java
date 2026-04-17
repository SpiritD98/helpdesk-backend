package com.helpdesk.helpdesk_backend.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.helpdesk.helpdesk_backend.model.CategoriaTicket;
import com.helpdesk.helpdesk_backend.repository.CategoriaTicketRepository;
import com.helpdesk.helpdesk_backend.service.CategoriaTicketService;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CategoriaTicketServiceImpl  implements CategoriaTicketService {

    private final CategoriaTicketRepository categoriaTicketRepository;

    public CategoriaTicketServiceImpl(CategoriaTicketRepository categoriaTicketRepository) {
        this.categoriaTicketRepository = categoriaTicketRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaTicket> listarTodos() {
        return categoriaTicketRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoriaTicket> buscarPorId(Long id) {
        return categoriaTicketRepository.findById(id);
    }

    @Override
    public CategoriaTicket guardar(CategoriaTicket categoriaTicket) {
        if (categoriaTicketRepository.existsByNombreAndEmpresaId(categoriaTicket.getNombre(), categoriaTicket.getEmpresa().getId())) {
            throw new RuntimeException("Error: Ya existe una categoría con ese nombre en la empresa.");
        }
        return categoriaTicketRepository.save(categoriaTicket);
    }

    @Override
    public CategoriaTicket actualizar(Long id, CategoriaTicket categoriaTicket) {
        CategoriaTicket categoriaExistente = categoriaTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Categoría no encontrada con el id " + id));

        if (!categoriaExistente.getNombre().equals(categoriaTicket.getNombre()) &&
            categoriaTicketRepository.existsByNombreAndEmpresaId(categoriaTicket.getNombre(), categoriaTicket.getEmpresa().getId())) {
            throw new RuntimeException("Error: Ya existe otra categoría con ese nombre en la empresa.");
        }

        categoriaExistente.setNombre(categoriaTicket.getNombre());
        categoriaExistente.setDescripcion(categoriaTicket.getDescripcion());
        categoriaExistente.setActiva(categoriaTicket.isActiva());
        categoriaExistente.setEmpresa(categoriaTicket.getEmpresa());

        return categoriaTicketRepository.save(categoriaExistente);
    }

    @Override
    public void eliminar(Long id) {
        if (!categoriaTicketRepository.existsById(id)) {
            throw new RuntimeException("Error: No se puede eliminar. Categoría no encontrada con id " + id);
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
