package com.helpdesk.helpdesk_backend.service;

import com.helpdesk.helpdesk_backend.model.Rol;
import com.helpdesk.helpdesk_backend.repository.RolRepository;
import com.helpdesk.helpdesk_backend.service.impl.RolServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceImplTest {

    // Mock del repositorio
    @Mock
    private RolRepository rolRepository;

    // Inyección del mock en el servicio
    @InjectMocks
    private RolServiceImpl rolService;

    @Test
    void buscarPorId_exitoso() {
        // Arrange
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMIN");
        
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        // Act: Buscamos el rol
        Optional<Rol> resultado = rolService.buscarPorId(1L);

        // Assert: Verificamos que se encontró
        assertTrue(resultado.isPresent());
        verify(rolRepository).findById(1L);
    }

    @Test
    void buscarPorId_noExiste() {
        // Arrange: Simulamos que no existe
        when(rolRepository.findById(1L)).thenReturn(Optional.empty());

        // Act: Buscamos el rol
        Optional<Rol> resultado = rolService.buscarPorId(1L);

        // Assert: Debe ser vacío
        assertFalse(resultado.isPresent());
        verify(rolRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorNombre_exitoso() {
        // Arrange: Creamos un rol de prueba
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMIN");
        
        when(rolRepository.findByNombre("ADMIN")).thenReturn(Optional.of(rol));

        // Act: Buscamos el rol por nombre
        Optional<Rol> resultado = rolService.buscarPorNombre("ADMIN");

        // Assert: Verificamos que se encontró
        assertTrue(resultado.isPresent());
        verify(rolRepository).findByNombre("ADMIN");
    }

    @Test
    void buscarPorNombre_noExiste() {
        // Arrange: Simulamos que no existe
        when(rolRepository.findByNombre("INEXISTENTE")).thenReturn(Optional.empty());

        // Act: Buscamos el rol
        Optional<Rol> resultado = rolService.buscarPorNombre("INEXISTENTE");

        // Assert: Debe ser vacío
        assertFalse(resultado.isPresent());
        verify(rolRepository).findByNombre("INEXISTENTE");
    }

    @Test
    void existePorNombre_existe() {
        // Arrange: Simulamos que el nombre existe
        when(rolRepository.existsByNombre("ADMIN")).thenReturn(true);

        // Act: Verificamos si existe
        boolean existe = rolService.existePorNombre("ADMIN");

        // Assert: Debe ser true
        assertTrue(existe);
        verify(rolRepository).existsByNombre("ADMIN");
    }

    @Test
    void existePorNombre_noExiste() {
        // Arrange: Simulamos que el nombre no existe
        when(rolRepository.existsByNombre("INEXISTENTE")).thenReturn(false);

        // Act: Verificamos si existe
        boolean existe = rolService.existePorNombre("INEXISTENTE");

        // Assert: Debe ser false
        assertFalse(existe);
        verify(rolRepository).existsByNombre("INEXISTENTE");
    }

    @Test
    void guardar_exitoso() {
        // Arrange: Creamos un rol a guardar
        Rol rolGuardar = new Rol();
        rolGuardar.setNombre("SUPERVISOR");
        
        // Simulamos que el nombre no existe
        when(rolRepository.existsByNombre("SUPERVISOR")).thenReturn(false);
        // Simulamos que al guardar devuelve el rol con ID asignado
        when(rolRepository.save(rolGuardar)).thenReturn(rolGuardar);

        // Act: Guardamos el rol
        Rol resultado = rolService.guardar(rolGuardar);

        // Assert: Verificamos que se guardó correctamente
        assertNotNull(resultado);
        verify(rolRepository).existsByNombre("SUPERVISOR");
        verify(rolRepository).save(rolGuardar);
    }

    @Test
    void guardar_nombreDuplicado() {
        // Arrange
        Rol rolDuplicado = new Rol();
        rolDuplicado.setNombre("ADMIN");
        // Simulamos que el nombre ya existe
        when(rolRepository.existsByNombre("ADMIN")).thenReturn(true);

        // Act & Assert: Verificamos que lanza excepción
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            rolService.guardar(rolDuplicado);
        });

        // Validamos el mensaje de error
        assertTrue(excepcion.getMessage().contains("Ya existe"));
    }

    @Test
    void actualizar_exitoso() {
        // Arrange: Creamos roles para la actualización
        Rol rolExistente = new Rol();
        rolExistente.setId(1L);
        rolExistente.setNombre("ADMIN");
        
        Rol rolNuevosDatos = new Rol();
        rolNuevosDatos.setNombre("ADMIN_ACTUALIZADO");
        
        // Simulamos búsqueda del rol existente
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolExistente));
        // Simulamos que el nuevo nombre no existe
        when(rolRepository.existsByNombre("ADMIN_ACTUALIZADO")).thenReturn(false);
        // Simulamos que al guardar devuelve el rol actualizado
        when(rolRepository.save(rolExistente)).thenReturn(rolExistente);

        // Act: Actualizamos el rol
        Rol resultado = rolService.actualizar(1L, rolNuevosDatos);

        // Assert: Verificamos que se actualizó correctamente
        assertNotNull(resultado);
        verify(rolRepository).findById(1L);
        verify(rolRepository).save(rolExistente);
    }

    @Test
    void actualizar_rolNoEncontrado() {
        // Arrange
        Rol rolNuevosDatos = new Rol();
        rolNuevosDatos.setNombre("NUEVO");
        
        when(rolRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert: Verificamos que lanza excepción
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            rolService.actualizar(1L, rolNuevosDatos);
        });

        // Validamos el mensaje de error
        assertTrue(excepcion.getMessage().contains("no encontrado"));
        verify(rolRepository).findById(1L);
        verify(rolRepository, never()).save(any());
    }

    @Test
    void actualizar_nombreDuplicado() {
        // Arrange: Creamos roles para la actualización
        Rol rolExistente = new Rol();
        rolExistente.setId(1L);
        rolExistente.setNombre("ADMIN");
        
        Rol rolNuevosDatos = new Rol();
        rolNuevosDatos.setNombre("USER"); // Intentamos cambiar a un nombre que ya existe
        
        // Simulamos búsqueda del rol existente
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolExistente));
        // Simulamos que el nuevo nombre ya existe en otro rol
        when(rolRepository.existsByNombre("USER")).thenReturn(true);

        // Act & Assert: Verificamos que lanza excepción
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            rolService.actualizar(1L, rolNuevosDatos);
        });

        // Validamos el mensaje de error
        assertTrue(excepcion.getMessage().contains("Ya existe"));
        verify(rolRepository).findById(1L);
        verify(rolRepository).existsByNombre("USER");
        verify(rolRepository, never()).save(any());
    }

    @Test
    void actualizar_mismoNombreActual() {
        // Arrange: Creamos roles para la actualización (mismo nombre)
        Rol rolExistente = new Rol();
        rolExistente.setId(1L);
        rolExistente.setNombre("USER");
        
        Rol rolNuevosDatos = new Rol();
        rolNuevosDatos.setNombre("USER"); // Mismo nombre
        
        // Simulamos búsqueda del rol existente
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolExistente));
        // Simulamos que al guardar devuelve el rol sin cambios
        when(rolRepository.save(rolExistente)).thenReturn(rolExistente);

        // Act: Actualizamos el rol con el mismo nombre
        Rol resultado = rolService.actualizar(1L, rolNuevosDatos);

        // Assert: Verificamos que se devuelve sin problemas
        assertNotNull(resultado);
        verify(rolRepository).findById(1L);
        verify(rolRepository).save(rolExistente);
    }

    @Test
    void eliminar_exitoso() {
        // Arrange: Simulamos que el rol existe
        when(rolRepository.existsById(1L)).thenReturn(true);
        doNothing().when(rolRepository).deleteById(1L);

        // Act: Eliminamos el rol
        assertDoesNotThrow(() -> rolService.eliminar(1L));

        // Assert: Verificamos que se eliminó correctamente
        verify(rolRepository).existsById(1L);
        verify(rolRepository).deleteById(1L);
    }

    @Test
    void eliminar_rolNoEncontrado() {
        // Arrange: Simulamos que el rol no existe
        when(rolRepository.existsById(1L)).thenReturn(false);

        // Act & Assert: Verificamos que lanza excepción
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            rolService.eliminar(1L);
        });

        // Validamos el mensaje de error
        assertTrue(excepcion.getMessage().contains("no encontrado"));
        verify(rolRepository).existsById(1L);
        verify(rolRepository, never()).deleteById(any());
    }

    @Test
    void listarTodos_exitoso(){
        // Arrange: Creamos una lista simulada de roles
        Rol rol1 = new Rol();
        rol1.setId(1L);
        rol1.setNombre("ADMIN");

        Rol rol2 = new Rol();
        rol2.setId(2L);
        rol2.setNombre("CLIENTE");

        List<Rol> listaRoles = Arrays.asList(rol1, rol2);

        // Simulamos que el repositorio devuelve esta lista
        when(rolRepository.findAll()).thenReturn(listaRoles);

        // Act: Llamamos al método listarTodos
        List<Rol> resultado = rolService.listarTodos();

        // Assert: Verificamos que la lista no sea nula y tenga el tamaño correcto
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("ADMIN", resultado.get(0).getNombre());
        assertEquals("CLIENTE", resultado.get(1).getNombre());
        
        // Verificamos que se llamó al repositorio
        verify(rolRepository).findAll();
    }
}
