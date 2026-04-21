package com.helpdesk.helpdesk_backend.service;

import com.helpdesk.helpdesk_backend.model.Ticket;
import com.helpdesk.helpdesk_backend.repository.TicketRepository;
import com.helpdesk.helpdesk_backend.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {
    //Simulamos el repositorio para no depender de la base de datos real
    @Mock
    private TicketRepository ticketRepository;
    //Inyectamos el repositorio falso en el service real
    @InjectMocks
    private TicketServiceImpl ticketService;
     //Inicializamos los mocks antes de cada test
    public TicketServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void guardar_generaCodigo() {
        Ticket ticket = new Ticket();
        ticket.setCodigo(null);//Simulamos que el ticket viene sin código
        //Decimos que el código no existe
        when(ticketRepository.existsByCodigo(anyString())).thenReturn(false);
        //Simulamos que al guardar, el repositorio devuelve el mismo ticket con un ID asignado
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArguments()[0]);
        Ticket resultado = ticketService.guardar(ticket);
        //Verificamos que el sistema generó un código automáticamente
        assertNotNull(resultado.getCodigo());
        verify(ticketRepository).save(ticket);//Confirmamos que se guardó
    }

    @Test
    void guardar_codigoDuplicado() {
        Ticket ticket = new Ticket();
        ticket.setCodigo("TCK-1234");
        //Simulamos que el código ya existe en la base de datos
        when(ticketRepository.existsByCodigo("TCK-1234")).thenReturn(true);
        //Verificamos que lanza error
        RuntimeException ex = assertThrows(RuntimeException.class,() -> ticketService.guardar(ticket));
        //Validamos el mensaje
        assertTrue(ex.getMessage().contains("Ya existe"));
    }

    @Test
    void buscarPorId_existente() {
        Ticket ticket = new Ticket();
        //Simulamos que el ticket existe
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        Optional<Ticket> resultado = ticketService.buscarPorId(1L);
        //Verificamos que encontró el ticket
        assertTrue(resultado.isPresent());
    }

    @Test
    void eliminar_noExiste() {
        //Simulamos que el ticket no existe
        when(ticketRepository.existsById(1L)).thenReturn(false);
        //Verificamos que lanza error al intentar eliminar un ticket que no existe
        RuntimeException ex = assertThrows(RuntimeException.class,() -> ticketService.eliminar(1L));
        assertTrue(ex.getMessage().contains("no encontrado"));
    }
}