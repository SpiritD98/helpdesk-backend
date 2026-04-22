package com.helpdesk.helpdesk_backend.service;

import com.helpdesk.helpdesk_backend.model.Usuario;
import com.helpdesk.helpdesk_backend.repository.UsuarioRepository;
import com.helpdesk.helpdesk_backend.service.impl.UsuarioServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    // --- Tests para GUARDAR ---

    @Test
    void guardar_exito() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@test.com");

        when(usuarioRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.guardar(usuario);
        assertNotNull(resultado);
        assertEquals("test@test.com", resultado.getEmail());
    }

    @Test
    void guardar_fallaPorEmailDuplicado() {
        Usuario usuario = new Usuario();
        usuario.setEmail("duplicado@test.com");

        when(usuarioRepository.existsByEmail("duplicado@test.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.guardar(usuario));
        assertTrue(ex.getMessage().contains("Ya existe un usuario con el email"));
    }

    // --- Tests para ACTUALIZAR (Manejando todas las ramas lógicas) ---

    @Test
    void actualizar_exito_mismoEmail_sinPassword() {
        // Camino 1: Mismo email (no busca en BD) y Password NULO (no actualiza password)
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setEmail("juan@test.com");
        existente.setPassword("clave_vieja");

        Usuario nuevosDatos = new Usuario();
        nuevosDatos.setEmail("juan@test.com"); // Mismo email
        nuevosDatos.setPassword(null); // Password nulo

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(existente);

        Usuario resultado = usuarioService.actualizar(1L, nuevosDatos);

        assertEquals("juan@test.com", resultado.getEmail());
        assertEquals("clave_vieja", resultado.getPassword()); // Mantuvo la clave vieja
        verify(usuarioRepository, never()).existsByEmail(anyString());
    }

    @Test
    void actualizar_exito_diferenteEmail_passwordVacio() {
        // Camino 2: Diferente email, pero libre en BD. Password VACÍO (no actualiza password)
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setEmail("juan@test.com");
        existente.setPassword("clave_vieja");

        Usuario nuevosDatos = new Usuario();
        nuevosDatos.setEmail("nuevo@test.com");
        nuevosDatos.setPassword(""); // Password vacío

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByEmail("nuevo@test.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(existente);

        Usuario resultado = usuarioService.actualizar(1L, nuevosDatos);

        assertEquals("nuevo@test.com", resultado.getEmail());
        assertEquals("clave_vieja", resultado.getPassword()); // Mantuvo la clave vieja
    }

    @Test
    void actualizar_exito_passwordValido() {
        // Camino 3: Actualiza todos los campos incluyendo el password
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setEmail("juan@test.com");

        Usuario nuevosDatos = new Usuario();
        nuevosDatos.setEmail("juan@test.com");
        nuevosDatos.setPassword("nueva_clave"); // Password válido

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(existente);

        Usuario resultado = usuarioService.actualizar(1L, nuevosDatos);

        assertEquals("nueva_clave", resultado.getPassword()); // Clave actualizada
    }

    @Test
    void actualizar_falla_diferenteEmailYDuplicado() {
        // Camino 4: Diferente email pero ya existe en BD -> Excepción
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setEmail("juan@test.com");

        Usuario nuevosDatos = new Usuario();
        nuevosDatos.setEmail("ocupado@test.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByEmail("ocupado@test.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.actualizar(1L, nuevosDatos));
        assertTrue(ex.getMessage().contains("Ya existe otro usuario"));
    }

    @Test
    void actualizar_fallaPorNoEncontrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> usuarioService.actualizar(1L, new Usuario()));
    }

    // --- Tests para ELIMINAR ---

    @Test
    void eliminar_exito() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        usuarioService.eliminar(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_fallaPorNoEncontrado() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> usuarioService.eliminar(1L));
        verify(usuarioRepository, never()).deleteById(anyLong());
    }

    // --- Tests para LISTAR y BUSCAR (Consultas de solo lectura) ---

    @Test
    void listarTodos() {
        when(usuarioRepository.findAll()).thenReturn(List.of(new Usuario()));
        assertFalse(usuarioService.listarTodos().isEmpty());
    }

    @Test
    void buscarPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        assertTrue(usuarioService.buscarPorId(1L).isPresent());
    }

    @Test
    void buscarPorEmail() {
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(new Usuario()));
        assertTrue(usuarioService.buscarPorEmail("test@test.com").isPresent());
    }

    @Test
    void existeEmail() {
        when(usuarioRepository.existsByEmail("test@test.com")).thenReturn(true);
        assertTrue(usuarioService.existeEmail("test@test.com"));
    }

    @Test
    void listarPorEmpresa() {
        when(usuarioRepository.findByEmpresaId(10L)).thenReturn(List.of(new Usuario()));
        assertFalse(usuarioService.listarPorEmpresa(10L).isEmpty());
    }

    @Test
    void listarPorRol() {
        when(usuarioRepository.findByRolId(2L)).thenReturn(List.of(new Usuario()));
        assertFalse(usuarioService.listarPorRol(2L).isEmpty());
    }

    @Test
    void listarActivosPorEmpresa() {
        when(usuarioRepository.findByEmpresaIdAndActivo(10L, true)).thenReturn(List.of(new Usuario()));
        assertFalse(usuarioService.listarActivosPorEmpresa(10L, true).isEmpty());
    }

    @Test
    void listarPorEmpresaYRol() {
        when(usuarioRepository.findByEmpresaIdAndRolId(10L, 2L)).thenReturn(List.of(new Usuario()));
        assertFalse(usuarioService.listarPorEmpresaYRol(10L, 2L).isEmpty());
    }

    @Test
    void listarPorEstado() {
        when(usuarioRepository.findByActivo(true)).thenReturn(List.of(new Usuario()));
        assertFalse(usuarioService.listarPorEstado(true).isEmpty());
    }
}
