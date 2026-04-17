package com.helpdesk.helpdesk_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Vamos a tratar las entidades en singular */

@Entity
@Table (name = "rol")
@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter
@Setter
public class Rol {
  
    @Id 
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    private Long id;

    /*Le agregue length 20 para evitar el tamaño por defecto */
    @Column(nullable = false, unique = true, length = 20)
    private String nombre;

}
