package com.helpdesk.helpdesk_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias_ticket")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategoriaTicket {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String nombre;

    private String descripcion ;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}
