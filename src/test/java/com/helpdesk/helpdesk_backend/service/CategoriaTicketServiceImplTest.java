package com.helpdesk.helpdesk_backend.service;

import com.helpdesk.helpdesk_backend.model.CategoriaTicket;
import com.helpdesk.helpdesk_backend.model.Empresa;
import com.helpdesk.helpdesk_backend.repository.CategoriaTicketRepository;
import com.helpdesk.helpdesk_backend.service.impl.CategoriaTicketServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaTicketServiceImplTest {

    @Mock
    private CategoriaTicketRepository categoriaTicketRepository;

    @InjectMocks
    private CategoriaTicketServiceImpl categoriaTicketService;

    private Empresa empresaBase;

    @BeforeEach
    void setUp() {
        // Inicializamos una empresa base para evitar NullPointerExceptions al hacer getEmpresa().getId()
        empresaBase = new Empresa();
        empresaBase.setId(10L);
    }

    // --- Tests para GUARDAR ---

    @Test
    void guardar_exito() {
        CategoriaTicket categoria = new CategoriaTicket();
        categoria.setNombre("Soporte");
        categoria.setEmpresa(empresaBase);

        when(categoriaTicketRepository.existsByNombreAndEmpresaId("Soporte", 10L)).thenReturn(false);
        when(categoriaTicketRepository.save(any(CategoriaTicket.class))).thenReturn(categoria);

        CategoriaTicket resultado = categoriaTicketService.guardar(categoria);
        assertNotNull(resultado);
        assertEquals("Soporte", resultado.getNombre());
    }

    @Test
    void guardar_fallaPorNombreDuplicado() {
        CategoriaTicket categoria = new CategoriaTicket();
        categoria.setNombre("Soporte");
        categoria.setEmpresa(empresaBase);

        when(categoriaTicketRepository.existsByNombreAndEmpresaId("Soporte", 10L)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> categoriaTicketService.guardar(categoria));
        assertTrue(ex.getMessage().contains("Ya existe una categoría"));
    }

    // --- Tests para ACTUALIZAR (Los 3 caminos del IF) ---

    @Test
    void actualizar_exito_mismoNombre() {
        // Camino 1: El nombre no cambia. Jacoco evalúa la 1ra parte del && como Falsa y se salta la 2da parte.
        CategoriaTicket existente = new CategoriaTicket();
        existente.setId(1L);
        existente.setNombre("Redes");
        existente.setEmpresa(empresaBase);

        CategoriaTicket nuevosDatos = new CategoriaTicket();
        nuevosDatos.setNombre("Redes"); // Mismo nombre
        nuevosDatos.setDescripcion("Nueva desc");
        nuevosDatos.setActiva(false);
        nuevosDatos.setEmpresa(empresaBase);

        when(categoriaTicketRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaTicketRepository.save(any(CategoriaTicket.class))).thenReturn(existente);

        CategoriaTicket resultado = categoriaTicketService.actualizar(1L, nuevosDatos);
        assertEquals("Redes", resultado.getNombre());
        assertEquals("Nueva desc", resultado.getDescripcion());
        assertFalse(resultado.isActiva());
        
        // Verificamos que NUNCA llamó a la base de datos para comprobar si existía (porque el nombre era igual)
        verify(categoriaTicketRepository, never()).existsByNombreAndEmpresaId(anyString(), anyLong());
    }

    @Test
    void actualizar_exito_diferenteNombrePeroNoDuplicado() {
        // Camino 2: Nombre diferente (1ra parte Verdadera), pero no existe en BD (2da parte Falsa).
        CategoriaTicket existente = new CategoriaTicket();
        existente.setId(1L);
        existente.setNombre("Redes");

        CategoriaTicket nuevosDatos = new CategoriaTicket();
        nuevosDatos.setNombre("Hardware"); // Diferente nombre
        nuevosDatos.setEmpresa(empresaBase);

        when(categoriaTicketRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaTicketRepository.existsByNombreAndEmpresaId("Hardware", 10L)).thenReturn(false);
        when(categoriaTicketRepository.save(any(CategoriaTicket.class))).thenReturn(existente);

        CategoriaTicket resultado = categoriaTicketService.actualizar(1L, nuevosDatos);
        assertEquals("Hardware", resultado.getNombre());
    }

    @Test
    void actualizar_falla_diferenteNombreYDuplicado() {
        // Camino 3: Nombre diferente (Verdadero) y SÍ existe en BD (Verdadero). Lanza excepción.
        CategoriaTicket existente = new CategoriaTicket();
        existente.setId(1L);
        existente.setNombre("Redes");

        CategoriaTicket nuevosDatos = new CategoriaTicket();
        nuevosDatos.setNombre("Hardware");
        nuevosDatos.setEmpresa(empresaBase);

        when(categoriaTicketRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaTicketRepository.existsByNombreAndEmpresaId("Hardware", 10L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> categoriaTicketService.actualizar(1L, nuevosDatos));
        assertTrue(ex.getMessage().contains("Ya existe una categoría"));
    }

    @Test
    void actualizar_fallaPorNoEncontrado() {
        when(categoriaTicketRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> categoriaTicketService.actualizar(1L, new CategoriaTicket()));
    }

    // --- Tests para ELIMINAR ---

    @Test
    void eliminar_exito() {
        when(categoriaTicketRepository.existsById(1L)).thenReturn(true);
        categoriaTicketService.eliminar(1L);
        verify(categoriaTicketRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_fallaPorNoEncontrado() {
        when(categoriaTicketRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> categoriaTicketService.eliminar(1L));
        verify(categoriaTicketRepository, never()).deleteById(anyLong());
    }

    // --- Tests para los métodos LISTAR / BUSCAR (Consultas directas) ---

    @Test
    void listarTodas() {
        when(categoriaTicketRepository.findAll()).thenReturn(List.of(new CategoriaTicket()));
        assertFalse(categoriaTicketService.listarTodas().isEmpty());
    }

    @Test
    void buscarPorId() {
        when(categoriaTicketRepository.findById(1L)).thenReturn(Optional.of(new CategoriaTicket()));
        assertTrue(categoriaTicketService.buscarPorId(1L).isPresent());
    }

    @Test
    void listarPorActiva() {
        when(categoriaTicketRepository.findByActiva(true)).thenReturn(List.of(new CategoriaTicket()));
        assertFalse(categoriaTicketService.listarPorActiva(true).isEmpty());
    }

    @Test
    void listarPorEmpresaId() {
        when(categoriaTicketRepository.findByEmpresaId(10L)).thenReturn(List.of(new CategoriaTicket()));
        assertFalse(categoriaTicketService.listarPorEmpresaId(10L).isEmpty());
    }

    @Test
    void listarPorEmpresaIdYActiva() {
        when(categoriaTicketRepository.findByEmpresaIdAndActiva(10L, true)).thenReturn(List.of(new CategoriaTicket()));
        assertFalse(categoriaTicketService.listarPorEmpresaIdYActiva(10L, true).isEmpty());
    }

    @Test
    void buscarPorNombreYEmpresaId() {
        when(categoriaTicketRepository.findByNombreAndEmpresaId("Soporte", 10L)).thenReturn(Optional.of(new CategoriaTicket()));
        assertTrue(categoriaTicketService.buscarPorNombreYEmpresaId("Soporte", 10L).isPresent());
    }

    @Test
    void existePorNombreYEmpresaId() {
        when(categoriaTicketRepository.existsByNombreAndEmpresaId("Soporte", 10L)).thenReturn(true);
        assertTrue(categoriaTicketService.existePorNombreYEmpresaId("Soporte", 10L));
    }
}