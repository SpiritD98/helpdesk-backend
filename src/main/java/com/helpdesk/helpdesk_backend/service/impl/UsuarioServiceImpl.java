package com.helpdesk.helpdesk_backend.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.helpdesk.helpdesk_backend.model.Usuario;
import com.helpdesk.helpdesk_backend.repository.UsuarioRepository;
import com.helpdesk.helpdesk_backend.service.UsuarioService;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService{

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Lista todos los usuarios.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su ID.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Guarda un nuevo usuario validando existencia de email.
     */
    @Override
    public Usuario guardar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Error: Ya existe un usuario con el email ingresado.");
        }
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza un usuario existente.
     */
    @Override
    public Usuario actualizar(Long id, Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado con el id " + id));

        if (!usuarioExistente.getEmail().equals(usuario.getEmail()) && usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Error: Ya existe otro usuario con el email ingresado.");
        }

        usuarioExistente.setNombres(usuario.getNombres());
        usuarioExistente.setApellidos(usuario.getApellidos());
        usuarioExistente.setEmail(usuario.getEmail());
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuarioExistente.setPassword(usuario.getPassword());
        }
        usuarioExistente.setTelefono(usuario.getTelefono());
        usuarioExistente.setActivo(usuario.isActivo());
        usuarioExistente.setEmpresa(usuario.getEmpresa());
        usuarioExistente.setRol(usuario.getRol());

        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Elimina un usuario por su ID.
     */
    @Override
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Error: No se puede eliminar. Usuario no encontrado con el id " + id);
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Busca un usuario usando su email de manera opcional.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Verifica la existencia de un email.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /**
     * Lista los usuarios que pertenecen a una empresa.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarPorEmpresa(Long empresaId) {
        return usuarioRepository.findByEmpresaId(empresaId);
    }

    /**
     * Lista todos los usuarios de un cierto rol.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarPorRol(Long rolId) {
        return usuarioRepository.findByRolId(rolId);
    }

    /**
     * Lista los usuarios activos de una empresa especifica.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarActivosPorEmpresa(Long empresaId, boolean activo) {
        return usuarioRepository.findByEmpresaIdAndActivo(empresaId, activo);
    }

    /**
     * Lista usuarios de una empresa filtrados por un rol especifico.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarPorEmpresaYRol(Long empresaId, Long rolId) {
        return usuarioRepository.findByEmpresaIdAndRolId(empresaId, rolId);
    }

    /**
     * Lista usuarios por un estado especifico (activos o inactivos).
     */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarPorEstado(boolean activo) {
        return usuarioRepository.findByActivo(activo);
    }
}
