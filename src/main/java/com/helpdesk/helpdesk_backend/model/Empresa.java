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
/* 
    Entidad que representa a las empresas clientes del sistema.
    Se utiliza el nombre en singular siguiendo las convenciones de JPA. 
*/
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, unique = true, length = 15)
    private String ruc;

    @Column(name = "correo_contacto", nullable = false, unique = true, length = 120)
    private String correoContacto;

    @Column(name = "telefono_contacto", nullable = false, length = 20)
    private String telefonoContacto;
    
    /* Estado lógico: Permite inhabilitar una empresa sin perder su historial transaccional. */
    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;
    
    /* Auditoría: Se registra automáticamente al insertar el registro y no permite modificaciones posteriores. */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
}
