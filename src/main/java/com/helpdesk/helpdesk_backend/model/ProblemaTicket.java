package com.helpdesk.helpdesk_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problema_ticket")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
/*
    Entidad que define los tipos de problemas específicos asociados a una categoría.
 */
public class ProblemaTicket {

    /* El valor del ID se autogenera en la BD 
        IDENTITY -> la bd se encargara del autoincremento*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    /* Siempre tendra un valor, limitamos su tamaño*/
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 250)
    private String descripcion;

    /* Borrado lógico: Indica si el registro está operativo. 
       Se utiliza para mantener la integridad referencial y el histórico de tickets. */
    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    /* @ManyToOne-> Muchos problemas pueden pertenecer a una sola categoria.
       FetchType.LAZY: Carga bajo demanda para optimizar el rendimiento de las consultas.
       JsonIgnoreProperties: Omite metadatos internos del Proxy de Hibernate durante la conversión a JSON. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CategoriaTicket categoria;
}
