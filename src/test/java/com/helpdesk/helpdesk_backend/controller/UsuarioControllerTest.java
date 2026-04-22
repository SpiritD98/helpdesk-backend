package com.helpdesk.helpdesk_backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.helpdesk.helpdesk_backend.model.Usuario;
import com.helpdesk.helpdesk_backend.service.UsuarioService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void listarTodos() {
        when(usuarioService.listarTodos()).thenReturn(List.of(new Usuario()));
        ResponseEntity<List<Usuario>> response = usuarioController.listarTodos();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void buscarPorId_existente() {
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        ResponseEntity<Usuario> response = usuarioController.buscarPorId(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarPorId_noExiste() {
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.empty());
        ResponseEntity<Usuario> response = usuarioController.buscarPorId(1L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void buscarPorEmail_existente() {
        when(usuarioService.buscarPorEmail("test@test.com")).thenReturn(Optional.of(new Usuario()));
        ResponseEntity<Usuario> response = usuarioController.buscarPorEmail("test@test.com");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarPorEmail_noExiste() {
        when(usuarioService.buscarPorEmail("test@test.com")).thenReturn(Optional.empty());
        ResponseEntity<Usuario> response = usuarioController.buscarPorEmail("test@test.com");
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void listarPorEmpresa() {
        when(usuarioService.listarPorEmpresa(10L)).thenReturn(List.of(new Usuario()));
        ResponseEntity<List<Usuario>> response = usuarioController.listarPorEmpresa(10L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void listarPorRol() {
        when(usuarioService.listarPorRol(2L)).thenReturn(List.of(new Usuario()));
        ResponseEntity<List<Usuario>> response = usuarioController.listarPorRol(2L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void listarPorEstado() {
        when(usuarioService.listarPorEstado(true)).thenReturn(List.of(new Usuario()));
        ResponseEntity<List<Usuario>> response = usuarioController.listarPorEstado(true);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void guardar() {
        Usuario usuario = new Usuario();
        when(usuarioService.guardar(any(Usuario.class))).thenReturn(usuario);
        
        ResponseEntity<Usuario> response = usuarioController.guardar(new Usuario());
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void actualizar() {
        Usuario usuario = new Usuario();
        when(usuarioService.actualizar(eq(1L), any(Usuario.class))).thenReturn(usuario);
        
        ResponseEntity<Usuario> response = usuarioController.actualizar(1L, new Usuario());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void eliminar() {
        doNothing().when(usuarioService).eliminar(1L);
        ResponseEntity<Void> response = usuarioController.eliminar(1L);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(usuarioService, times(1)).eliminar(1L);
    }
}
