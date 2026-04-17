package com.helpdesk.helpdesk_backend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "empresa")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /*Se agrego el max de caracteres */
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, unique = true, length = 15)
    private String ruc;

    @Column(name = "correo_contacto", nullable = false, unique = true, length = 120)
    private String correoContacto;

    /*Se agrego este campo*/
    @Column(name = "telefono_contacto", nullable = false, length = 20)
    private String telefonoContacto;
    
    /*Se cambio el nombre de estado a activo */
    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;
    
    /*Se cambio de notacion, permitira rellenar el campo automaticamente al insertarla en la bd*/
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
}