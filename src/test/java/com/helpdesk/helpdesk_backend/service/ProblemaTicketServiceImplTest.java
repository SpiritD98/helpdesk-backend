package com.helpdesk.helpdesk_backend.service;

import com.helpdesk.helpdesk_backend.model.ProblemaTicket;
import com.helpdesk.helpdesk_backend.repository.ProblemaTicketRepository;
import com.helpdesk.helpdesk_backend.service.impl.ProblemaTicketServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProblemaTicketServiceImplTest {
    //Simula la base de datos
    @Mock
    private ProblemaTicketRepository repository;
    //Inyecta el repositorio falso en el service real
    @InjectMocks
    private ProblemaTicketServiceImpl service;
    //Activa Mockito
    public ProblemaTicketServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void guardar_ok() {
        ProblemaTicket p = new ProblemaTicket();//Crea objeto
        when(repository.save(p)).thenReturn(p);//Simula guardado
        //Llama al método a probar
        ProblemaTicket resultado = service.guardar(p);
        assertNotNull(resultado);//Verifica que devolvio algo
        verify(repository).save(p);//verifica que se guardo
    }

    @Test
    void buscarPorId() {
        ProblemaTicket p = new ProblemaTicket();
        //Simula que existe un ticket con id 1
        when(repository.findById(1L)).thenReturn(Optional.of(p));
        //Ejecuta busquedad
        Optional<ProblemaTicket> resultado = service.buscarPorId(1L);
        assertTrue(resultado.isPresent());//Verifica que existe
    }

    @Test
    void actualizar_ok() {
        ProblemaTicket existente = new ProblemaTicket();//Simula problema existente
        ProblemaTicket nuevo = new ProblemaTicket();//Simula datos nuevos
        nuevo.setNombre("Nuevo");
        //Simula que existe en la BD
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any())).thenReturn(existente);//Simula guardado
        //Ejecuta lógica
        ProblemaTicket resultado = service.actualizar(1L, nuevo);
        assertEquals("Nuevo", resultado.getNombre());//Verifica que se actualizó
        verify(repository).save(existente);
    }

    @Test
    void actualizar_noExiste() {
        //Simula que no existe el problema
        when(repository.findById(1L)).thenReturn(Optional.empty());
        //Simula datos nuevos a actualizar
        ProblemaTicket nuevo = new ProblemaTicket();
        //Verifica eque lanza error al no encontrar el problema
        assertThrows(RuntimeException.class, () -> service.actualizar(1L, nuevo));
    }

    @Test
    void eliminar_noExiste() {
        when(repository.existsById(1L)).thenReturn(false);//Simula que no existe el problema
        //Verifica el error al intentar eliminar un problema que no existe
        assertThrows(RuntimeException.class, () -> service.eliminar(1L));
    }
}