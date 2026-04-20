package com.helpdesk.helpdesk_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

<<<<<<< HEAD
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
=======
import jakarta.persistence.*;
import lombok.*;
>>>>>>> 8eaeb62e55a2ce6864a09dd235e5bf1dc2ecb45c

@Entity
@Table(name = "problema_ticket")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProblemaTicket {
<<<<<<< HEAD

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Se agrego longitud */
=======
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

>>>>>>> 8eaeb62e55a2ce6864a09dd235e5bf1dc2ecb45c
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 250)
    private String descripcion;

<<<<<<< HEAD
    /* Evitaremos que los problemas sean borrados, solo desactivarlos */
=======
>>>>>>> 8eaeb62e55a2ce6864a09dd235e5bf1dc2ecb45c
    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CategoriaTicket categoria;
<<<<<<< HEAD
=======

>>>>>>> 8eaeb62e55a2ce6864a09dd235e5bf1dc2ecb45c
}
