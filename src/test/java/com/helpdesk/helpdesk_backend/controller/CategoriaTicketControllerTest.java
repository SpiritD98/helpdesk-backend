package com.helpdesk.helpdesk_backend.controller;

import com.helpdesk.helpdesk_backend.model.CategoriaTicket;
import com.helpdesk.helpdesk_backend.service.CategoriaTicketService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaTicketControllerTest {

    @Mock
    private CategoriaTicketService categoriaTicketService;

    @InjectMocks
    private CategoriaTicketController categoriaTicketController;

    @Test
    void listarTodos() {
        when(categoriaTicketService.listarTodas()).thenReturn(List.of(new CategoriaTicket()));
        ResponseEntity<List<CategoriaTicket>> response = categoriaTicketController.listarTodos();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void buscarPorId_existente() {
        when(categoriaTicketService.buscarPorId(1L)).thenReturn(Optional.of(new CategoriaTicket()));
        ResponseEntity<CategoriaTicket> response = categoriaTicketController.buscarPorId(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarPorId_noExiste() {
        when(categoriaTicketService.buscarPorId(1L)).thenReturn(Optional.empty());
        ResponseEntity<CategoriaTicket> response = categoriaTicketController.buscarPorId(1L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void listarPorEmpresa() {
        when(categoriaTicketService.listarPorEmpresaId(10L)).thenReturn(List.of(new CategoriaTicket()));
        ResponseEntity<List<CategoriaTicket>> response = categoriaTicketController.listarPorEmpresa(10L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void guardar() {
        CategoriaTicket categoria = new CategoriaTicket();
        when(categoriaTicketService.guardar(any(CategoriaTicket.class))).thenReturn(categoria);
        
        ResponseEntity<CategoriaTicket> response = categoriaTicketController.guardar(new CategoriaTicket());
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void actualizar() {
        CategoriaTicket categoria = new CategoriaTicket();
        when(categoriaTicketService.actualizar(eq(1L), any(CategoriaTicket.class))).thenReturn(categoria);
        
        ResponseEntity<CategoriaTicket> response = categoriaTicketController.actualizar(1L, new CategoriaTicket());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void eliminar() {
        doNothing().when(categoriaTicketService).eliminar(1L);
        ResponseEntity<Void> response = categoriaTicketController.eliminar(1L);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoriaTicketService, times(1)).eliminar(1L);
    }
}