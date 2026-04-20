package com.helpdesk.helpdesk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.helpdesk.helpdesk_backend.model.Usuario;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository  extends JpaRepository <Usuario, Long > {

    // Buscar usuario por email.
    // Este método será clave para login, seguridad y validaciones.
    Optional <Usuario> findByEmail(String email) ;

    // Verificar si ya existe un usuario con ese email.
    // Importante para evitar correos duplicados al registrar usuarios.
    boolean existsByEmail(String email);

    // Listar todos los usuarios de una empresa específica.
    // Útil en un sistema SaaS donde cada empresa tiene sus propios usuarios.
    List<Usuario> findByEmpresaId(Long empresaId);

    // Listar usuarios según el rol.
    // Sirve, por ejemplo, para obtener solo agentes o solo clientes.
    List<Usuario> findByRolId(Long rolId);

    // Listar usuarios de una empresa filtrando además si están activos.
    // Ejemplo: usuarios activos de la empresa 1.
    List<Usuario> findByEmpresaIdAndActivo(Long empresaId, boolean activo);

    // Listar usuarios de una empresa con un rol específico.
    // Ejemplo: agentes de soporte de una empresa.
    List<Usuario> findByEmpresaIdAndRolId(Long empresaId, Long rolId);

    // Listar usuarios según si están activos o inactivos.
    // Puede usarse en el panel de administración.
    List<Usuario> findByActivo(boolean activo);

}
