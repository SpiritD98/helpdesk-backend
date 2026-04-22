package com.helpdesk.helpdesk_backend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        // Inicializamos el manejador antes de cada prueba
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleRuntimeException_deberiaRetornarBadRequest() {
        // Arrange: Creamos una RuntimeException con un mensaje específico
        String mensajeError = "Error de validación de negocio simulado";
        RuntimeException ex = new RuntimeException(mensajeError);

        // Act: Llamamos directamente al método del manejador
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(ex);

        // Assert: Verificamos el código de estado (400)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        // Verificamos el contenido del mapa JSON
        Map<String, Object> body = response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals(mensajeError, body.get("message"));
        
        // Verificamos que el timestamp se haya generado
        assertTrue(body.containsKey("timestamp"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleGeneralException_deberiaRetornarInternalServerError() {
        // Arrange: Creamos una Exception general (ej. falla de base de datos no controlada)
        String mensajeError = "Error inesperado del servidor";
        Exception ex = new Exception(mensajeError);

        // Act: Llamamos directamente al método del manejador
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGeneralException(ex);

        // Assert: Verificamos el código de estado (500)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        // Verificamos el contenido del mapa JSON
        Map<String, Object> body = response.getBody();
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals(mensajeError, body.get("message"));
        
        // Verificamos que el timestamp se haya generado
        assertTrue(body.containsKey("timestamp"));
        assertNotNull(body.get("timestamp"));
    }
}