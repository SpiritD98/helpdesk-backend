package com.helpdesk.helpdesk_backend.controller;

import com.helpdesk.helpdesk_backend.model.Empresa;
import com.helpdesk.helpdesk_backend.service.EmpresaService;
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
class EmpresaControllerTest {

    @Mock
    private EmpresaService empresaService;

    @InjectMocks
    private EmpresaController empresaController;
    /* */
    @Test
    void listarTodos() {
        // Simula que el servicio retorna una lista con una empresa
        when(empresaService.listarTodos()).thenReturn(List.of(new Empresa()));
        // Ejecuta el método del controlador
        ResponseEntity<List<Empresa>> response = empresaController.listarTodos();
        // Verifica que el código HTTP sea 200 OK y que el body contenga 1 elemento
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void buscarPorId_existente() {
        // Simula que el servicio encuentra una empresa con id 1
        when(empresaService.buscarPorId(1L)).thenReturn(Optional.of(new Empresa()));
         // Ejecuta el método del controlador con el id 1
        ResponseEntity<Empresa> response = empresaController.buscarPorId(1L);

        // Verifica que el código HTTP sea 200 OK y que el body no sea nulo
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarPorId_noExiste() {
         // Simula que el servicio no encuentra ninguna empresa con id 1
        when(empresaService.buscarPorId(1L)).thenReturn(Optional.empty());
        // Ejecuta el método del controlador con el id 1
        ResponseEntity<Empresa> response = empresaController.buscarPorId(1L);
        // Verifica que el código HTTP sea 404 NOT FOUND
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void buscarPorRuc_existente() {
        // Simula que el servicio encuentra una empresa con el RUC indicado
        when(empresaService.buscarPorRuc("20123456789")).thenReturn(Optional.of(new Empresa()));
        // Ejecuta el método del controlador con el RUC indicado
        ResponseEntity<Empresa> response = empresaController.buscarPorRuc("20123456789");
        // Verifica que el código HTTP sea 200 OK y que el body no sea nulo
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarPorRuc_noExiste() {
        // Simula que el servicio no encuentra ninguna empresa con el RUC indicado
        when(empresaService.buscarPorRuc("20123456789")).thenReturn(Optional.empty());
        // Ejecuta el método del controlador con el RUC indicado
        ResponseEntity<Empresa> response = empresaController.buscarPorRuc("20123456789");
        // Verifica que el código HTTP sea 404 NOT FOUND
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void buscarPorCorreo_existente() {
        // Simula que el servicio encuentra una empresa con el correo indicado
        when(empresaService.buscarPorCorreoContacto("empresa@correo.com")).thenReturn(Optional.of(new Empresa()));
        // Ejecuta el método del controlador con el correo indicado
        ResponseEntity<Empresa> response = empresaController.buscarPorCorreo("empresa@correo.com");
        // Verifica que el código HTTP sea 200 OK y que el body no sea nulo
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarPorCorreo_noExiste() {
         // Simula que el servicio no encuentra ninguna empresa con el correo indicado
        when(empresaService.buscarPorCorreoContacto("empresa@correo.com")).thenReturn(Optional.empty());
         // Ejecuta el método del controlador con el correo indicado
        ResponseEntity<Empresa> response = empresaController.buscarPorCorreo("empresa@correo.com");
         // Verifica que el código HTTP sea 404 NOT FOUND
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void guardar() {
        // Crea una empresa de prueba a retornar por el mock
        Empresa empresa = new Empresa();
        // Simula que el servicio guarda cualquier empresa y la retorna
        when(empresaService.guardar(any(Empresa.class))).thenReturn(empresa);
        // Ejecuta el método del controlador con una empresa vacía
        ResponseEntity<Empresa> response = empresaController.guardar(new Empresa());
        // Verifica que el código HTTP sea 201 CREATED y que el body no sea nulo
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void actualizar() {
        // Crea una empresa de prueba a retornar por el mock
        Empresa empresa = new Empresa();
        // Simula que el servicio actualiza la empresa con id 1 y retorna la empresa actualizada
        when(empresaService.actualizar(eq(1L), any(Empresa.class))).thenReturn(empresa);
        // Ejecuta el método del controlador con el id 1 y una empresa vacía
        ResponseEntity<Empresa> response = empresaController.actualizar(1L, new Empresa());
         // Verifica que el código HTTP sea 200 OK y que el body no sea nulo
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void eliminar() {
        // Simula que el servicio elimina la empresa con id 1 sin retornar nada
        doNothing().when(empresaService).eliminar(1L);
        // Ejecuta el método del controlador con el id 1
        ResponseEntity<Void> response = empresaController.eliminar(1L);
        // Verifica que el código HTTP sea 204 NO CONTENT
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // Verifica que el servicio fue llamado exactamente una vez con el id 1
        verify(empresaService, times(1)).eliminar(1L);
    }
}