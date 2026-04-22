package com.helpdesk.helpdesk_backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.helpdesk.helpdesk_backend.model.enums.EstadoTicket;
import com.helpdesk.helpdesk_backend.model.enums.PrioridadTicket;

class TicketTest {

    @Test
    void prePersist_conValoresNulos_deberiaAsignarValoresPorDefectoYGenerarCodigo() {
        // Arrange: Un ticket completamente nuevo y vacío
        Ticket ticket = new Ticket();

        // Act: Simulamos el guardado de JPA llamando al hook
        ticket.prePersist();

        // Assert: Verificamos que se asignaron los valores por defecto
        assertEquals(EstadoTicket.ABIERTO, ticket.getEstado(), "El estado por defecto debe ser ABIERTO");
        assertEquals(PrioridadTicket.MEDIA, ticket.getPrioridad(), "La prioridad por defecto debe ser MEDIA");
        
        // Verificamos la generación del código
        assertNotNull(ticket.getCodigo(), "El código no debe ser nulo");
        assertTrue(ticket.getCodigo().startsWith("TCK-"), "El código debe empezar con TCK-");
        
        // Validamos que tenga la fecha actual en el código (ej. TCK-20260422-...)
        LocalDate hoy = LocalDate.now();
        String fechaParte = String.format("%04d%02d%02d", hoy.getYear(), hoy.getMonthValue(), hoy.getDayOfMonth());
        assertTrue(ticket.getCodigo().contains(fechaParte), "El código debe contener la fecha actual");
    }

    @Test
    void prePersist_conValoresYaAsignados_noDeberiaSobrescribir() {
        // Arrange: Un ticket que ya tiene datos asignados (ej. por una actualización o asignación manual previa)
        Ticket ticket = new Ticket();
        ticket.setEstado(EstadoTicket.EN_PROGRESO);
        ticket.setPrioridad(PrioridadTicket.ALTA);
        ticket.setCodigo("TCK-CUSTOM-123");

        // Act: Llamamos al hook
        ticket.prePersist();

        // Assert: Verificamos que respetó los valores originales y no los pisó
        assertEquals(EstadoTicket.EN_PROGRESO, ticket.getEstado());
        assertEquals(PrioridadTicket.ALTA, ticket.getPrioridad());
        assertEquals("TCK-CUSTOM-123", ticket.getCodigo());
    }

    @Test
    void prePersist_conCodigoEnBlanco_deberiaGenerarNuevoCodigo() {
        // Arrange: Un ticket donde el código es un string vacío (cubriendo el 'isBlank()')
        Ticket ticket = new Ticket();
        ticket.setCodigo("   "); // Espacios en blanco

        // Act
        ticket.prePersist();

        // Assert: Verificamos que generó un código válido reemplazando el espacio en blanco
        assertNotNull(ticket.getCodigo());
        assertFalse(ticket.getCodigo().isBlank());
        assertTrue(ticket.getCodigo().startsWith("TCK-"));
    }
}
