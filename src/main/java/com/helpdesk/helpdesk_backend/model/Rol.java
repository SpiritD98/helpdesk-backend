package com.helpdesk.helpdesk_backend.model;

import jakarta.persistence.*;
import lombok.*;
/* Vamos a tratar las entidades en singular */
@Entity
@Table (name = "rol")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Rol {
  
    @Id 
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    private Long id;

    /*Le agregue length 20 para evitar el tamaño por defecto */
    @Column(nullable = false, unique = true, length = 20)
    private String nombre;

}
