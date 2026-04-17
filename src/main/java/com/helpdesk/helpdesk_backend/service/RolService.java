package com.helpdesk.helpdesk_backend.service;

import java.util.List;
import java.util.Optional;

import com.helpdesk.helpdesk_backend.model.Rol;

public interface RolService {
    
    // CRUD Básico
    List<Rol> listarTodos();
    Optional<Rol> buscarPorId(Long id);
    Rol guardar(Rol rol);
    Rol actualizar(Long id, Rol rol);
    void eliminar(Long id);

    // Métodos Custom
    Optional<Rol> buscarPorNombre(String nombre);
    boolean existePorNombre(String nombre);
}

