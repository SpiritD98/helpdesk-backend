package com.helpdesk.helpdesk_backend.service;

import java.util.List;
import java.util.Optional;

import com.helpdesk.helpdesk_backend.model.CategoriaTicket;

public interface CategoriaTicketService {
    //Crear categoria
    CategoriaTicket crear(CategoriaTicket categoria);
    //Devuelve categorias de la bd
    List<CategoriaTicket> listarTodas();
    //Busca categoria por id, usa optional para evitar errores de inexistencia
    Optional<CategoriaTicket> obtenerPorId(Long id);
    //Actualiza categoria existente
    CategoriaTicket actualizar(Long id, CategoriaTicket categoria);
    //Desactiva id (NO ELIMINA)
    void eliminarLogico(Long id); 

    // Filtros
    List<CategoriaTicket> listarPorEmpresa(Long empresaId); //Por empresa
    List<CategoriaTicket> listarActivas(boolean activa); //Por estado activo
    List<CategoriaTicket> listarPorEmpresaYActiva(Long empresaId, boolean activa); //Por empresa y estado activo

}