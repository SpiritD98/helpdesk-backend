package com.helpdesk.helpdesk_backend.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.helpdesk.helpdesk_backend.model.Rol;
import com.helpdesk.helpdesk_backend.repository.RolRepository;
import com.helpdesk.helpdesk_backend.service.RolService;

@Service
@Transactional
public class RolServiceImpl implements RolService{
    
    private final RolRepository rolRepository;

    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    /**
     * Lista todos los roles.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    /**
     * Busca un rol por su ID.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> buscarPorId(Long id) {
        return rolRepository.findById(id);
    }

    /**
     * Guarda un nuevo rol verificando nombre duplicado.
     */
    @Override
    public Rol guardar(Rol rol) {
        if (rolRepository.existsByNombre(rol.getNombre())) {
            throw new RuntimeException("Error: Ya existe un rol con el nombre ingresado.");
        }
        return rolRepository.save(rol);
    }

    /**
     * Actualiza un rol existente.
     */
    @Override
    public Rol actualizar(Long id, Rol rol) {
        Rol rolExistente = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado con el id " + id));

        if (!rolExistente.getNombre().equals(rol.getNombre()) && rolRepository.existsByNombre(rol.getNombre())) {
            throw new RuntimeException("Error: Ya existe otro rol con el nombre ingresado.");
        }

        rolExistente.setNombre(rol.getNombre());
        return rolRepository.save(rolExistente);
    }

    /**
     * Elimina un rol por su ID.
     */
    @Override
    public void eliminar(Long id) {
        if (!rolRepository.existsById(id)) {
            throw new RuntimeException("Error: No se puede eliminar. Rol no encontrado con el id " + id);
        }
        rolRepository.deleteById(id);
    }

    /**
     * Busca un rol por su nombre.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    /**
     * Verifica si existe un rol por su nombre.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }
}
