package com.helpdesk.helpdesk_backend.controller;

import com.helpdesk.helpdesk_backend.model.ProblemaTicket;
import com.helpdesk.helpdesk_backend.service.ProblemaTicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProblemaTicketControllerTest {

    @Mock
    private ProblemaTicketService problemaTicketService;

    @InjectMocks
    private ProblemaTicketController problemaTicketController;

    @Test
    void listarTodos() {
        when(problemaTicketService.listarTodos()).thenReturn(List.of(new ProblemaTicket()));
        
        ResponseEntity<List<ProblemaTicket>> response = problemaTicketController.listarTodos();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void buscarPorId_existente() {
        ProblemaTicket problema = new ProblemaTicket();
        when(problemaTicketService.buscarPorId(1L)).thenReturn(Optional.of(problema));
        
        ResponseEntity<ProblemaTicket> response = problemaTicketController.buscarPorId(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarPorId_noExiste() {
        when(problemaTicketService.buscarPorId(1L)).thenReturn(Optional.empty());
        
        ResponseEntity<ProblemaTicket> response = problemaTicketController.buscarPorId(1L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void listarPorCategoria() {
        when(problemaTicketService.listarPorCategoriaId(10L)).thenReturn(List.of(new ProblemaTicket(), new ProblemaTicket()));
        
        ResponseEntity<List<ProblemaTicket>> response = problemaTicketController.listarPorCategoria(10L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void guardar() {
        ProblemaTicket problema = new ProblemaTicket();
        when(problemaTicketService.guardar(any(ProblemaTicket.class))).thenReturn(problema);
        
        ResponseEntity<ProblemaTicket> response = problemaTicketController.guardar(new ProblemaTicket());
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void actualizar() {
        ProblemaTicket problema = new ProblemaTicket();
        when(problemaTicketService.actualizar(eq(1L), any(ProblemaTicket.class))).thenReturn(problema);
        
        ResponseEntity<ProblemaTicket> response = problemaTicketController.actualizar(1L, new ProblemaTicket());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void eliminar() {
        doNothing().when(problemaTicketService).eliminar(1L);
        
        ResponseEntity<Void> response = problemaTicketController.eliminar(1L);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(problemaTicketService, times(1)).eliminar(1L);
    }
}