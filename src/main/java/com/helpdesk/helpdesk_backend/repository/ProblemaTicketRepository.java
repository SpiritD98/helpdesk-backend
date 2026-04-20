package com.helpdesk.helpdesk_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.helpdesk.helpdesk_backend.model.ProblemaTicket;

public interface ProblemaTicketRepository extends JpaRepository<ProblemaTicket, Long> {

    // Listar problemas de una categoría específica.
    // Útil para organizar problemas por categorías.
    List<ProblemaTicket> findByCategoriaId(Long empresaId);

    // Listar problemas de una empresa específica.
    // En un sistema SaaS cada empresa tendrá sus propios problemas.
    List<ProblemaTicket> findByActivo(boolean activo);
}
