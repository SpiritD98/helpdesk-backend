package com.helpdesk.helpdesk_backend.model;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTicket estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadTicket prioridad;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    /* Se usa LAZY para evitar cargar datos innecesarios; luego se controla desde DTO */
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agente_asignado_id")
    private Usuario agenteAsignado;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaTicket categoria;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();

        if (this.estado == null) {
            this.estado = EstadoTicket.ABIERTO;
        }

        if (this.prioridad == null) {
            this.prioridad = PrioridadTicket.MEDIA;
        }
    }

    @PreUpdate
    protected void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
