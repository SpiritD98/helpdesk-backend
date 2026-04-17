package com.helpdesk.helpdesk_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.helpdesk.helpdesk_backend.model.Empresa;

public interface EmpresaRepository  extends JpaRepository<Empresa, Long> {

    // TODO: Buscar una empresa por su RUC.
    // Se usará para validaciones y para evitar registrar empresas repetidas.
    Optional<Empresa> findByRuc(String ruc);

    // TODO: Buscar una empresa por su correo de contacto.
    // Útil para validaciones o búsquedas administrativas.
    Optional<Empresa> findByCorreoContacto(String correoContacto);

    // TODO: Verificar si ya existe una empresa con ese RUC.
    // Ayuda a evitar duplicados antes de guardar.
    boolean existsByRuc(String ruc);

    // TODO: Verificar si ya existe una empresa con ese correo de contacto.
    // También se usará para validar registros repetidos.
    boolean existsByCorreoContacto(String correoContacto);

}
