package com.helpdesk.helpdesk_backend.service;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.helpdesk.helpdesk_backend.model.Usuario;
import com.helpdesk.helpdesk_backend.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id " + id));
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public List<Usuario> findByEmpresaId(Long empresaId) {
        return usuarioRepository.findByEmpresaId(empresaId);
    }

    @Override
    public List<Usuario> findByEmpresaIdAndRolNombre(Long empresaId, String rolNombre) {
        return usuarioRepository.findByEmpresaIdAndRolNombre(empresaId, rolNombre);
    }

    @Override
    public List<Usuario> findByEstado(boolean estado) {
        return usuarioRepository.findByEstado(estado);
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario update(Long id, Usuario usuario) {
        Usuario existing = findById(id);
        existing.setNombres(usuario.getNombres());
        existing.setApellidos(usuario.getApellidos());
        existing.setEmail(usuario.getEmail());
        existing.setPassword(usuario.getPassword());
        existing.setEstado(usuario.isEstado());
        existing.setEmpresa(usuario.getEmpresa());
        existing.setRol(usuario.getRol());
        return usuarioRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Usuario existing = findById(id);
        usuarioRepository.delete(existing);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
