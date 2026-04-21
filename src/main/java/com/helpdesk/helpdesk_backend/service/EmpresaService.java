package com.helpdesk.helpdesk_backend.service;

import java.util.List;
import java.util.Optional;

import com.helpdesk.helpdesk_backend.model.Empresa;

public interface EmpresaService {

    //Devuelve empresas de la bd
    List<Empresa> listarTodos();

    //Busca empresa por id, usa optional para evitar errores de inexistencia
    Optional<Empresa> buscarPorId(Long id);

    //Crear nueva empresa
    Empresa guardar(Empresa empresa);

    //Actualizar empresa existente
    Empresa actualizar(Long id, Empresa empresa);

    //Desactivar empresa (NO ELIMINA)
    void eliminar(Long id);

    // Filtros
    //Busca empresa por ruc, usa optional para evitar errores de inexistencia
    Optional<Empresa> buscarPorRuc(String ruc);

    //Busca empresa por correo de contacto, usa optional para evitar errores de inexistencia
    Optional<Empresa> buscarPorCorreoContacto(String correoContacto);

    //Verifica si existe empresa por ruc, para evitar duplicados
    boolean existePorRuc(String ruc);

    //Verifica si existe empresa por correo de contacto, para evitar duplicados
    boolean existePorCorreoContacto(String correoContacto);
}
