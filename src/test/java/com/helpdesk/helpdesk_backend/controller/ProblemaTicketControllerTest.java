package com.helpdesk.helpdesk_backend.controller;

import com.helpdesk.helpdesk_backend.model.ProblemaTicket;
import com.helpdesk.helpdesk_backend.service.ProblemaTicketService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProblemaTicketControllerTest {
    //Crea un servicio falso
    @Mock
    private ProblemaTicketService service;
    //Inyecta el mock dentro del controller
    @InjectMocks
    private ProblemaTicketController controller;
    //Inicializa los mocks antes de ejecutar pruebas
    public ProblemaTicketControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarTodos() {
        //Crea una lista simulada con un objeto
        List<ProblemaTicket> lista = List.of(new ProblemaTicket());
        when(service.listarTodos()).thenReturn(lista);//Define comportamiento del mock:  devuelve lista cuando se llame
        //Ejecuta el método real del controller
        ResponseEntity<List<ProblemaTicket>> response = controller.listarTodos();
        //Verifica que responde 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());//Verifica que el body no sea null
        assertEquals(1, response.getBody().size());//Verifica que hay 1 elemento
    }

    @Test
    void buscarPorId_existente() {
        ProblemaTicket p = new ProblemaTicket();
        //Simula que el registro existe
        when(service.buscarPorId(1L)).thenReturn(Optional.of(p));
        //Ejecuta el método
        ResponseEntity<ProblemaTicket> response = controller.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());//Debe devolver 200 OK
        assertNotNull(response.getBody());// Debe tener datos
    }

    @Test
    void buscarPorId_noExiste() {
        when(service.buscarPorId(1L)).thenReturn(Optional.empty()); //Simula que no existe
        ResponseEntity<ProblemaTicket> response = controller.buscarPorId(1L);
        //Debe devolver 404 Not Found
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void guardar() {
        ProblemaTicket p = new ProblemaTicket();
        when(service.guardar(p)).thenReturn(p);//Simula guardado
        //Ejecutamos el método real del controller
        ResponseEntity<ProblemaTicket> response = controller.guardar(p);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());//Debe devolver 201
        assertNotNull(response.getBody());
    }

    @Test
    void eliminar() {
        doNothing().when(service).eliminar(1L);//Simula eliminación sin error
        ResponseEntity<Void> response = controller.eliminar(1L);
        //Debe devolver 204 No Content
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service, times(1)).eliminar(1L);//Verifica que el método se ejecutó una vez
    }
}