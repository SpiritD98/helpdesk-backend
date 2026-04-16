package com.helpdesk.helpdesk_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (name = "roles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Rol {
  
    @Id 
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
