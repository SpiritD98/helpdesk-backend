package com.helpdesk.helpdesk_backend.service;

import java.util.List;
import java.util.Optional;

import com.helpdesk.helpdesk_backend.model.ProblemaTicket;

public interface ProblemaTicketService {

    //Devuelve problemas de la bd
    List<ProblemaTicket> listarTodos();

    //Busca problema por id, usa optional para evitar errores de inexistencia
    Optional<ProblemaTicket> buscarPorId(Long id);

    //Crear nuevo problema
    ProblemaTicket guardar(ProblemaTicket problemaTicket);

    //Actualizar problema existente
    ProblemaTicket actualizar(Long id, ProblemaTicket problemaTicket);

    //Desactivar problema (NO ELIMINA)
    void eliminar(Long id);

    // Filtros
    //Lista problemas por categoria
    List<ProblemaTicket> listarPorCategoriaId(Long categoriaId);

    //Lista problemas por problema activo/inactivo
    List<ProblemaTicket> listarPorActivo(boolean activo);
}
