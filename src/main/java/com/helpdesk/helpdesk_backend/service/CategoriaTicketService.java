package com.helpdesk.helpdesk_backend.service;

import java.util.List;
import java.util.Optional;

import com.helpdesk.helpdesk_backend.model.CategoriaTicket;

public interface CategoriaTicketService {

    //Crear categoria
    CategoriaTicket guardar(CategoriaTicket categoriaTicket);

    //Devuelve categorias de la bd
    List<CategoriaTicket> listarTodas();

    //Busca categoria por id, usa optional para evitar errores de inexistencia
    Optional<CategoriaTicket> buscarPorId(Long id);

    //Actualiza categoria existente
    CategoriaTicket actualizar(Long id, CategoriaTicket categoriaTicket);

    //Desactiva id (NO ELIMINA)
    void eliminar(Long id); 

    // Filtros
    //Lista categorias por estado activo/inactivo
    List<CategoriaTicket> listarPorActiva(boolean activa);
    //Lista categorias por empresa 
    List<CategoriaTicket> listarPorEmpresaId(Long empresaId);
    //Lista categorias por empresa y estado activo/inactivo 
    List<CategoriaTicket> listarPorEmpresaIdYActiva(Long empresaId, boolean activa);
    //Busca categoria por nombre y empresa, usa optional para evitar errores de inexistencia 
    Optional<CategoriaTicket> buscarPorNombreYEmpresaId(String nombre, Long empresaId);
    //Verifica si existe categoria por nombre y empresa, para evitar duplicados
    boolean existePorNombreYEmpresaId(String nombre, Long empresaId);

}