package com.helpdesk.helpdesk_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.helpdesk.helpdesk_backend.model.ProblemaTicket;

public interface ProblemaTicketRepository extends JpaRepository < ProblemaTicket, Long > {


    List<ProblemaTicket> findByCategoriaId(Long categoriaId);

    List<ProblemaTicket> findByActivo(boolean activo);
}
