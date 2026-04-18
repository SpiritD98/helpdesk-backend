package com.helpdesk.helpdesk_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categoria_ticket")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
/* Vamos a tratar las entidades en singular */
public class CategoriaTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Se agrego longitud */
    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(length = 250)
    private String descripcion ;

    /* Evitaremos que las categorias sean borradas, solo desactivarlas */
    @Builder.Default
    @Column(nullable = false)
    private boolean activa = true;

    /* Se agrego relacion con empresa, una categoria pertenece a una empresa 
    Se cambio a LAZY, para posteriormente controlarlo desde los DTO  y evitar datos innecesarios*/
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}
