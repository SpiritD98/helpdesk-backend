package com.helpdesk.helpdesk_backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.helpdesk.helpdesk_backend.model.enums.EstadoTicket;
import com.helpdesk.helpdesk_backend.model.enums.PrioridadTicket;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "ticket")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
/* Vamos a tratar las entidades en singular */
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Se agrego la columna codigo, para buscar, mostrar y mencionar */
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String titulo;
    
    /* Para evitar que quede vacio, agregamos nullable = false */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 20)
    private String telefonoReportante;

    @Column(length = 120)
    private String correoReportante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTicket estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadTicket prioridad;
    
    @Column(columnDefinition = "TEXT")
    private String justifiacionCierre; // Justificación para el cierre del ticket, solo se llena si el estado es CERRADO

    private String imagenCierre; // URL o ruta de la imagen que evidencia el cierre del ticket, solo se llena si el estado es CERRADO
    
    /* Se agrego esta columna para controlar el cierre del ticket */
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    /* Se usa LAZY para evitar cargar datos innecesarios; luego se controla desde DTO */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agente_asignado_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario agenteAsignado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CategoriaTicket categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problema_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ProblemaTicket problema;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Empresa empresa;

    @PrePersist
    public void prePersist() {

        if (this.estado == null) {
            this.estado = EstadoTicket.ABIERTO;
        }

        if (this.prioridad == null) {
            this.prioridad = PrioridadTicket.MEDIA;
        }

        if (this.codigo == null || this.codigo.isBlank()) {
            LocalDate hoy = LocalDate.now();
            String fechaParte = String.format("%04d%02d%02d", hoy.getYear(), hoy.getMonthValue(), hoy.getDayOfMonth());
            this.codigo = "TCK-" + fechaParte + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

}
