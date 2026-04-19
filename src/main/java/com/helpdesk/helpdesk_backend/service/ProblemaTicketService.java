package com.helpdesk.helpdesk_backend.service;

import java.util.List;
import java.util.Optional;

import com.helpdesk.helpdesk_backend.model.ProblemaTicket;

public interface ProblemaTicketService {


     List<ProblemaTicket> listarTodos();
    Optional<ProblemaTicket> buscarPorId(Long id);
    ProblemaTicket guardar(ProblemaTicket problemaTicket);
    ProblemaTicket actualizar(Long id, ProblemaTicket problemaTicket);
    void eliminar(Long id);

    List<ProblemaTicket> listarPorCategoriaId(Long categoriaId);
    List<ProblemaTicket> listarPorActivo(boolean activo);

}
