package com.helpdesk.helpdesk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.helpdesk.helpdesk_backend.model.Usuario;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository  extends JpaRepository <Usuario, Long > {

    Optional <Usuario> findByEmail(String email) ;

    boolean existsByEmail(String email);

    List<Usuario> findByEmpresaId(Long empresaId);

    List<Usuario> findByEmpresaIdAndRolNombre(Long empresaId, String rolNombre);

    List<Usuario> findByEstado(boolean estado);




}
