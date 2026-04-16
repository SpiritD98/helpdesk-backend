package com.helpdesk.helpdesk_backend.service;



import java.util.List;
import java.util.Optional;

import com.helpdesk.helpdesk_backend.model.CategoriaTicket;




public interface CategoriaTicketService {

    List<CategoriaTicket> listarTodos();
    Optional<CategoriaTicket> buscarPorId(Long id);
    CategoriaTicket guardar(CategoriaTicket categoriaTicket);
    CategoriaTicket actualizar(Long id, CategoriaTicket categoriaTicket);
    void eliminar(Long id);

    List<CategoriaTicket> listarPorActiva(boolean activa);
    List<CategoriaTicket> listarPorEmpresaId(Long empresaId);
    List<CategoriaTicket> listarPorEmpresaIdYActiva(Long empresaId, boolean activa);
    Optional<CategoriaTicket> buscarPorNombreYEmpresaId(String nombre, Long empresaId);
    boolean existePorNombreYEmpresaId(String nombre, Long empresaId);



}
