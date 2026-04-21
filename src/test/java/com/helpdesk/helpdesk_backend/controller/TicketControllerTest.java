package com.helpdesk.helpdesk_backend.controller;

import com.helpdesk.helpdesk_backend.model.Ticket;
import com.helpdesk.helpdesk_backend.service.TicketService;

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

// @ExtendWith reemplaza a MockitoAnnotations.openMocks(this) en JUnit 5
@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    // Mock del servicio
    @Mock
    private TicketService ticketService;

    // Inyección del mock en el controller
    @InjectMocks
    private TicketController ticketController;

    @Test
    void listarTodos() {
        List<Ticket> lista = List.of(new Ticket());//Creamos lista falsa con 1 ticket
        when(ticketService.listarTodos()).thenReturn(lista);//Devuelve lista
        //Ejecutamos el método real del controller
        ResponseEntity<List<Ticket>> response = ticketController.listarTodos();
        //Válidamos que responde 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());//Verificamos si hay contenido
        assertEquals(1, response.getBody().size());//Validamos que haya un elemento
    }

    @Test
    void buscarPorId_existente() {
        Ticket ticket = new Ticket(); // creamos objeto prueba
        //simulamos que el ticket existe
        when(ticketService.buscarPorId(1L)).thenReturn(Optional.of(ticket));
        //ejecutamos
        ResponseEntity<Ticket> response = ticketController.buscarPorId(1L);
        //Debe devolver 200 OK y el body debe existir
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarPorId_noExiste() {
        //Siulamos que no existe
        when(ticketService.buscarPorId(1L)).thenReturn(Optional.empty());
        //Ejecutamos
        ResponseEntity<Ticket> response = ticketController.buscarPorId(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());//Debe devolver 404 NOT FOUND
    }

    @Test
    void guardar() {
        Ticket ticket = new Ticket(); //creamos objeto
        when(ticketService.guardar(ticket)).thenReturn(ticket);//Simulamos guardado correcto
        ResponseEntity<Ticket> response = ticketController.guardar(ticket); //ejecutamos
        //Deve devolver 201 created
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());//Debe tener contenido
    }

    @Test
    void eliminar() {
        //Simulamos eliminación exitosa
        doNothing().when(ticketService).eliminar(1L);
        ResponseEntity<Void> response = ticketController.eliminar(1L); //ejecutamos
        //Debe devovler 204 no content
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        //Verificamos que el me´todo fue llamado 1 vez
        verify(ticketService, times(1)).eliminar(1L);
    }

    @Test
    void buscarPorCodigo_existente(){
        Ticket ticket = new Ticket();
        when(ticketService.buscarPorCodigo("TCK-123")).thenReturn(Optional.of(ticket));
        ResponseEntity<Ticket> response = ticketController.buscarPorCodigo("TCK-123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarPorCodigo_noExiste(){
        when(ticketService.buscarPorCodigo("TCK-999")).thenReturn(Optional.empty());
        ResponseEntity<Ticket> response = ticketController.buscarPorCodigo("TCK-999");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void listarPorEmpresa() {
        List<Ticket> lista = List.of(new Ticket(), new Ticket());
        when(ticketService.listarPorEmpresaId(1L)).thenReturn(lista);

        ResponseEntity<List<Ticket>> response = ticketController.listarPorEmpresa(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void listarPorCliente(){
        List<Ticket> lista = List.of(new Ticket());
        when(ticketService.listarPorClienteId(1L)).thenReturn(lista);

        ResponseEntity<List<Ticket>> response = ticketController.listarPorCliente(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listarPorAgente(){
        List<Ticket> lista = List.of(new Ticket());
        when(ticketService.listarPorAgenteAsignadoId(2L)).thenReturn(lista);

        ResponseEntity<List<Ticket>> response = ticketController.listarPorAgente(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void actualizar(){
        Ticket ticketAActualizar = new Ticket();
        Ticket ticketActualizado = new Ticket();
        ticketActualizado.setTitulo("Actualizado");

        when(ticketService.actualizar(1L, ticketAActualizar)).thenReturn(ticketActualizado);

        ResponseEntity<Ticket> response = ticketController.actualizar(1L, ticketAActualizar);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Actualizado", response.getBody().getTitulo());
    }
}