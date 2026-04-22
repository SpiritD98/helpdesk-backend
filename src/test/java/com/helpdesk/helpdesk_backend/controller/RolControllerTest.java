package com.helpdesk.helpdesk_backend.controller;

import com.helpdesk.helpdesk_backend.model.Rol;
import com.helpdesk.helpdesk_backend.service.RolService;

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
class RolControllerTest {

    // Mock del servicio
    @Mock
    private RolService rolService;

    // Inyección del mock en el controller
    @InjectMocks
    private RolController rolController;

    @Test
    void listarTodos() {
        List<Rol> listaRoles = List.of(new Rol());
        
        when(rolService.listarTodos()).thenReturn(listaRoles);

        // Act: Ejecutamos el método del controller
        ResponseEntity<List<Rol>> response = rolController.listarTodos();

        // Assert: Validamos que responde 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void buscarPorId_existente() {
        // Arrange: Creamos un rol de prueba
        Rol rol = new Rol();
        
        when(rolService.buscarPorId(1L)).thenReturn(Optional.of(rol));

        // Act: Ejecutamos el método
        ResponseEntity<Rol> response = rolController.buscarPorId(1L);

        // Assert: Debe devolver 200 OK y el body debe existir
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarPorId_noExiste() {
        // Arrange: Simulamos que no existe
        when(rolService.buscarPorId(1L)).thenReturn(Optional.empty());

        // Act: Ejecutamos el método
        ResponseEntity<Rol> response = rolController.buscarPorId(1L);

        // Assert: Debe devolver 404 NOT FOUND
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void buscarPorNombre_existente() {
        // Arrange: Creamos un rol de prueba
        Rol rol = new Rol(1L, "Administrador");
        
        when(rolService.buscarPorNombre("Administrador")).thenReturn(Optional.of(rol));

        // Act: Ejecutamos el método
        ResponseEntity<Rol> response = rolController.buscarPorNombre("Administrador");

        // Assert: Debe devolver 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Administrador", response.getBody().getNombre());
    }

    @Test
    void buscarPorNombre_noExiste() {
        // Arrange: Simulamos que no existe
        when(rolService.buscarPorNombre("No existe Rol")).thenReturn(Optional.empty());

        // Act: Ejecutamos el método
        ResponseEntity<Rol> response = rolController.buscarPorNombre("No existe Rol");

        // Assert: Debe devolver 404 NOT FOUND
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void guardar() {
        // Arrange: Creamos un rol a guardar
        Rol rolGuardar = new Rol();

        when(rolService.guardar(rolGuardar)).thenReturn(rolGuardar);

        // Act: Ejecutamos el método
        ResponseEntity<Rol> response = rolController.guardar(rolGuardar);

        // Assert: Debe devolver 201 CREATED
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void actualizar() {
        // Arrange: Creamos roles para actualizar
        Rol rolActualizar = new Rol();
        Rol rolAActualizar = new Rol();
        rolAActualizar.setNombre("Rol Actualizado");
        
        when(rolService.actualizar(1L, rolActualizar)).thenReturn(rolAActualizar);

        // Act: Ejecutamos el método
        ResponseEntity<Rol> response = rolController.actualizar(1L, rolActualizar);

        // Assert: Debe devolver 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Rol Actualizado", response.getBody().getNombre());
    }

    @Test
    void eliminar() {
        // Arrange: Simulamos eliminación exitosa
        doNothing().when(rolService).eliminar(1L);

        // Act: Ejecutamos el método
        ResponseEntity<Void> response = rolController.eliminar(1L);

        // Assert: Debe devolver 204 NO CONTENT
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(rolService, times(1)).eliminar(1L);
    }
}
