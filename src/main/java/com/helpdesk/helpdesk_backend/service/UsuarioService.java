package com.helpdesk.helpdesk_backend.service;

import java.util.List;
import java.util.Optional;

import com.helpdesk.helpdesk_backend.model.Usuario;

public interface UsuarioService {

    List<Usuario> findAll();

    Usuario findById(Long id);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByEmpresaId(Long empresaId);

    List<Usuario> findByEmpresaIdAndRolNombre(Long empresaId, String rolNombre);

    List<Usuario> findByEstado(boolean estado);

    Usuario save(Usuario usuario);

    Usuario update(Long id, Usuario usuario);

    void deleteById(Long id);

    boolean existsByEmail(String email);
}

