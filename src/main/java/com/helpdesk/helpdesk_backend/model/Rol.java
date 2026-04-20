package com.helpdesk.helpdesk_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (name = "rol")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
/*
    Entidad que define los roles de acceso en el sistema (ej. ADMIN, USER).
    Se mantiene en singular para respetar las convenciones de JPA.
 */
public class Rol {
  
    @Id 
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    private Long id;

    /* unique = true: No pueden existir dos roles con el mismo nombre.
       length = 20: Espacio optimizado, suficiente para nombres de roles estándar. */
    @Column(nullable = false, unique = true, length = 20)
    private String nombre;

}
