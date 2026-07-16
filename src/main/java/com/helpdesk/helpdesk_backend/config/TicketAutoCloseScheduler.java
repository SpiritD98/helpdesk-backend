package com.helpdesk.helpdesk_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.helpdesk.helpdesk_backend.service.TicketService;

/**
 * Tarea programada que cierra automáticamente los tickets que llevan más de
 * {@code TicketServiceImpl.DIAS_PARA_AUTO_CIERRE} días en estado RESUELTO sin
 * que el cliente haya calificado ni reabrido.
 *
 * Se ejecuta todos los días a las 03:00 (hora de Perú, America/Lima). El horario
 * se eligió fuera de horas pico para no sumar carga a la BD.
 */
@Component
public class TicketAutoCloseScheduler {

    private static final Logger log = LoggerFactory.getLogger(TicketAutoCloseScheduler.class);

    private final TicketService ticketService;

    public TicketAutoCloseScheduler(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Lima")
    public void cerrarResueltosInactivos() {
        try {
            int cerrados = ticketService.cerrarResueltosAutomaticamente();
            if (cerrados > 0) {
                log.info("Auto-cierre: {} ticket(s) cerrados por inactividad del cliente (>=7 días en RESUELTO).",
                        cerrados);
            }
        } catch (Exception e) {
            log.error("Error ejecutando el auto-cierre de tickets resueltos", e);
        }
    }
}
