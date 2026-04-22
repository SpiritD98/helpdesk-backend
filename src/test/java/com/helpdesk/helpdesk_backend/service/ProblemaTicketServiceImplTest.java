package com.helpdesk.helpdesk_backend.service;

import com.helpdesk.helpdesk_backend.model.CategoriaTicket;
import com.helpdesk.helpdesk_backend.model.ProblemaTicket;
import com.helpdesk.helpdesk_backend.repository.ProblemaTicketRepository;
import com.helpdesk.helpdesk_backend.service.impl.ProblemaTicketServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProblemaTicketServiceImplTest {

    //Simula la base de datos
    @Mock
    private ProblemaTicketRepository problemaTicketRepository;

    //Inyecta el repositorio falso en el service real
    @InjectMocks
    private ProblemaTicketServiceImpl problemaTicketService;

    @Test
    void listarTodos() {
        //Simula que hay 2 problemas en la base de datos
        when(problemaTicketRepository.findAll()).thenReturn(List.of(new ProblemaTicket(), new ProblemaTicket()));
        //Ejecuta el método a probar
        List<ProblemaTicket> resultado = problemaTicketService.listarTodos();
        assertEquals(2, resultado.size());//Verifica que devolvió 2 problemas
    }

    @Test
    void guardar() {
        ProblemaTicket problema = new ProblemaTicket();
        problema.setNombre("Problema de Red");
        when(problemaTicketRepository.save(any(ProblemaTicket.class))).thenReturn(problema);
        
        ProblemaTicket resultado = problemaTicketService.guardar(new ProblemaTicket());
        assertEquals("Problema de Red", resultado.getNombre());
    }
    

    @Test
    void buscarPorId_existente() {
        ProblemaTicket problema = new ProblemaTicket();
        problema.setId(1L);
        when(problemaTicketRepository.findById(1L)).thenReturn(Optional.of(problema));
        
        Optional<ProblemaTicket> resultado = problemaTicketService.buscarPorId(1L);
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    void buscarPorId_noExiste() {
        when(problemaTicketRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<ProblemaTicket> resultado = problemaTicketService.buscarPorId(1L);
        assertFalse(resultado.isPresent());
    }

    @Test
    void actualizar_existente() {
        // Arrange
        ProblemaTicket existente = new ProblemaTicket();
        existente.setId(1L);
        existente.setNombre("Viejo Nombre");

        ProblemaTicket nuevosDatos = new ProblemaTicket();
        nuevosDatos.setNombre("Nuevo Nombre");
        nuevosDatos.setDescripcion("Nueva Descripcion");
        nuevosDatos.setActivo(false);
        nuevosDatos.setCategoria(new CategoriaTicket()); // Opcional, pero da cobertura a setCategoria

        when(problemaTicketRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(problemaTicketRepository.save(any(ProblemaTicket.class))).thenReturn(existente);

        // Act
        ProblemaTicket resultado = problemaTicketService.actualizar(1L, nuevosDatos);

        // Assert
        assertEquals("Nuevo Nombre", resultado.getNombre());
        assertEquals("Nueva Descripcion", resultado.getDescripcion());
        assertFalse(resultado.isActivo());
    }

    @Test
    void actualizar_noExiste() {
        when(problemaTicketRepository.findById(1L)).thenReturn(Optional.empty());
        
        RuntimeException ex = assertThrows(RuntimeException.class, () -> problemaTicketService.actualizar(1L, new ProblemaTicket()));
        assertTrue(ex.getMessage().contains("Problema no encontrado"));
    }

    @Test
    void eliminar_existente() {
        when(problemaTicketRepository.existsById(1L)).thenReturn(true);
        problemaTicketService.eliminar(1L);
        verify(problemaTicketRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_noExiste() {
        when(problemaTicketRepository.existsById(1L)).thenReturn(false);
        
        RuntimeException ex = assertThrows(RuntimeException.class, () -> problemaTicketService.eliminar(1L));
        assertTrue(ex.getMessage().contains("No se puede eliminar"));
        verify(problemaTicketRepository, never()).deleteById(anyLong());
    }

    @Test
    void listarPorCategoriaId() {
        when(problemaTicketRepository.findByCategoriaId(10L)).thenReturn(List.of(new ProblemaTicket()));
        List<ProblemaTicket> resultado = problemaTicketService.listarPorCategoriaId(10L);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void listarPorActivo() {
        when(problemaTicketRepository.findByActivo(true)).thenReturn(List.of(new ProblemaTicket()));
        List<ProblemaTicket> resultado = problemaTicketService.listarPorActivo(true);
        assertFalse(resultado.isEmpty());
    }
}