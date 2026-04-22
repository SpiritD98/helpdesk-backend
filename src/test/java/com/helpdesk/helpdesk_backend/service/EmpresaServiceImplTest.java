package com.helpdesk.helpdesk_backend.service;

import com.helpdesk.helpdesk_backend.model.Empresa;
import com.helpdesk.helpdesk_backend.repository.EmpresaRepository;
import com.helpdesk.helpdesk_backend.service.impl.EmpresaServiceImpl;

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
class EmpresaServiceImplTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpresaServiceImpl empresaService;

    @Test
    void listarTodos() {
        // Simula que el repositorio retorna una lista con una empresa
        when(empresaRepository.findAll()).thenReturn(List.of(new Empresa()));

        // Ejecuta el método del servicio
        List<Empresa> result = empresaService.listarTodos();

        // Verifica que la lista no sea nula y contenga 1 elemento
        assertEquals(1, result.size());
        verify(empresaRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_existente() {
        // Simula que el repositorio encuentra una empresa con id 1
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(new Empresa()));

        // Ejecuta el método del servicio
        Optional<Empresa> result = empresaService.buscarPorId(1L);

        // Verifica que el resultado esté presente
        assertTrue(result.isPresent());
        verify(empresaRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_noExiste() {
        // Simula que el repositorio no encuentra ninguna empresa con id 1
        when(empresaRepository.findById(1L)).thenReturn(Optional.empty());

        // Ejecuta el método del servicio
        Optional<Empresa> result = empresaService.buscarPorId(1L);

        // Verifica que el resultado esté vacío
        assertFalse(result.isPresent());
        verify(empresaRepository, times(1)).findById(1L);
    }

    @Test
    void guardar_exitoso() {
        // Crea una empresa de prueba con RUC y correo únicos
        Empresa empresa = new Empresa();
        empresa.setRuc("20123456789");
        empresa.setCorreoContacto("empresa@correo.com");

        // Simula que no existe ninguna empresa con el mismo RUC ni correo
        when(empresaRepository.existsByRuc("20123456789")).thenReturn(false);
        when(empresaRepository.existsByCorreoContacto("empresa@correo.com")).thenReturn(false);
        when(empresaRepository.save(empresa)).thenReturn(empresa);

        // Ejecuta el método del servicio
        Empresa result = empresaService.guardar(empresa);

        // Verifica que la empresa retornada no sea nula y se haya guardado
        assertNotNull(result);
        verify(empresaRepository, times(1)).save(empresa);
    }

    @Test
    void guardar_rucDuplicado() {
        // Crea una empresa con un RUC que ya existe en el repositorio
        Empresa empresa = new Empresa();
        empresa.setRuc("20123456789");

        // Simula que ya existe una empresa con el mismo RUC
        when(empresaRepository.existsByRuc("20123456789")).thenReturn(true);

        // Verifica que se lanza una excepción al intentar guardar
        RuntimeException ex = assertThrows(RuntimeException.class, () -> empresaService.guardar(empresa));
        assertEquals("Error: Ya existe una empresa con el mismo RUC.", ex.getMessage());
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void guardar_correoDuplicado() {
        // Crea una empresa con un correo que ya existe en el repositorio
        Empresa empresa = new Empresa();
        empresa.setRuc("20123456789");
        empresa.setCorreoContacto("empresa@correo.com");

        // Simula que el RUC no existe pero el correo sí
        when(empresaRepository.existsByRuc("20123456789")).thenReturn(false);
        when(empresaRepository.existsByCorreoContacto("empresa@correo.com")).thenReturn(true);

        // Verifica que se lanza una excepción al intentar guardar
        RuntimeException ex = assertThrows(RuntimeException.class, () -> empresaService.guardar(empresa));
        assertEquals("Error: Ya existe una empresa con el mismo correo de contacto.", ex.getMessage());
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void actualizar_exitoso() {
        // Crea la empresa existente y la empresa con los nuevos datos
        Empresa empresaExistente = new Empresa();
        empresaExistente.setRuc("20123456789");
        empresaExistente.setCorreoContacto("empresa@correo.com");

        Empresa empresaActualizada = new Empresa();
        empresaActualizada.setNombre("Nueva Empresa");
        empresaActualizada.setRuc("20123456789");
        empresaActualizada.setCorreoContacto("empresa@correo.com");
        empresaActualizada.setTelefonoContacto("999999999");
        empresaActualizada.setActivo(true);

        // Simula que el repositorio encuentra la empresa y la guarda
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresaExistente));
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresaExistente);

        // Ejecuta el método del servicio
        Empresa result = empresaService.actualizar(1L, empresaActualizada);

        // Verifica que la empresa retornada no sea nula y se haya guardado
        assertNotNull(result);
        verify(empresaRepository, times(1)).save(any(Empresa.class));
    }

    @Test
    void actualizar_conNuevosDatosValidos_exitoso(){
        // Arrage: La empresa existente con datos viejos
        Empresa empresaExistente = new Empresa();
        empresaExistente.setId(1L);
        empresaExistente.setRuc("20000000000"); // RUC viejo
        empresaExistente.setCorreoContacto("viejo@correo.com"); // Correo viejo
        
        // La empresa actualizada con nuevos datos válidos
        Empresa empresaActualizada = new Empresa();
        empresaActualizada.setNombre("Nueva Empresa Tech");
        empresaActualizada.setRuc("20999999999"); // RUC nuevo
        empresaActualizada.setCorreoContacto("nuevo@correo.com"); // Correo nuevo
        empresaActualizada.setTelefonoContacto("123456789");
        empresaActualizada.setActivo(true);

        // Simulamos que encuentra la empresa original
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresaExistente));

        // Simulamos que los NUEVOS datos no existen en la BD (Validación exitosa)
        when(empresaRepository.existsByRuc("20999999999")).thenReturn(false);
        when(empresaRepository.existsByCorreoContacto("nuevo@correo.com")).thenReturn(false);
        
        // Simulamos el guardado
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresaExistente);

        // Act: Ejecutamos la actualización
        Empresa result = empresaService.actualizar(1L, empresaActualizada);

        // Assert: Verificamos que se guardó la empresa con los nuevos datos
        assertNotNull(result);
        verify(empresaRepository).findById(1L);
        verify(empresaRepository).existsByRuc("20999999999");
        verify(empresaRepository).existsByCorreoContacto("nuevo@correo.com");
        verify(empresaRepository).save(any(Empresa.class));
    }


    @Test
    void actualizar_noExiste() {
        // Simula que el repositorio no encuentra la empresa con id 1
        when(empresaRepository.findById(1L)).thenReturn(Optional.empty());

        // Verifica que se lanza una excepción al intentar actualizar
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> empresaService.actualizar(1L, new Empresa()));
        assertEquals("Error: Empresa no encontrada con id: 1", ex.getMessage());
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void actualizar_rucDuplicado() {
        // Crea la empresa existente con un RUC diferente al que se quiere actualizar
        Empresa empresaExistente = new Empresa();
        empresaExistente.setRuc("20000000000");
        empresaExistente.setCorreoContacto("empresa@correo.com");

        Empresa empresaActualizada = new Empresa();
        empresaActualizada.setRuc("20123456789");
        empresaActualizada.setCorreoContacto("empresa@correo.com");

        // Simula que el nuevo RUC ya está en uso por otra empresa
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresaExistente));
        when(empresaRepository.existsByRuc("20123456789")).thenReturn(true);

        // Verifica que se lanza una excepción al intentar actualizar
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> empresaService.actualizar(1L, empresaActualizada));
        assertEquals("Error: Ya existe una empresa con el mismo RUC.", ex.getMessage());
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void actualizar_correoDuplicado() {
        // Crea la empresa existente con un correo diferente al que se quiere actualizar
        Empresa empresaExistente = new Empresa();
        empresaExistente.setRuc("20123456789");
        empresaExistente.setCorreoContacto("viejo@correo.com");

        Empresa empresaActualizada = new Empresa();
        empresaActualizada.setRuc("20123456789");
        empresaActualizada.setCorreoContacto("nuevo@correo.com");

        // Simula que el nuevo correo ya está en uso por otra empresa
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresaExistente));
        when(empresaRepository.existsByCorreoContacto("nuevo@correo.com")).thenReturn(true);

        // Verifica que se lanza una excepción al intentar actualizar
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> empresaService.actualizar(1L, empresaActualizada));
        assertEquals("Error: Ya existe una empresa con el mismo correo de contacto.", ex.getMessage());
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void eliminar_exitoso() {
        // Simula que la empresa con id 1 existe en el repositorio
        when(empresaRepository.existsById(1L)).thenReturn(true);

        // Ejecuta el método del servicio
        empresaService.eliminar(1L);

        // Verifica que el repositorio eliminó la empresa exactamente una vez
        verify(empresaRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_noExiste() {
        // Simula que la empresa con id 1 no existe en el repositorio
        when(empresaRepository.existsById(1L)).thenReturn(false);

        // Verifica que se lanza una excepción al intentar eliminar
        RuntimeException ex = assertThrows(RuntimeException.class, () -> empresaService.eliminar(1L));
        assertEquals("Error: No se puede eliminar. Empresa no encontrada con id: 1", ex.getMessage());
        verify(empresaRepository, never()).deleteById(any());
    }

    @Test
    void buscarPorRuc_existente() {
        // Simula que el repositorio encuentra una empresa con el RUC indicado
        when(empresaRepository.findByRuc("20123456789")).thenReturn(Optional.of(new Empresa()));

        // Ejecuta el método del servicio
        Optional<Empresa> result = empresaService.buscarPorRuc("20123456789");

        // Verifica que el resultado esté presente
        assertTrue(result.isPresent());
        verify(empresaRepository, times(1)).findByRuc("20123456789");
    }

    @Test
    void buscarPorRuc_noExiste() {
        // Simula que el repositorio no encuentra ninguna empresa con el RUC indicado
        when(empresaRepository.findByRuc("20123456789")).thenReturn(Optional.empty());

        // Ejecuta el método del servicio
        Optional<Empresa> result = empresaService.buscarPorRuc("20123456789");

        // Verifica que el resultado esté vacío
        assertFalse(result.isPresent());
        verify(empresaRepository, times(1)).findByRuc("20123456789");
    }

    @Test
    void buscarPorCorreoContacto_existente() {
        // Simula que el repositorio encuentra una empresa con el correo indicado
        when(empresaRepository.findByCorreoContacto("empresa@correo.com")).thenReturn(Optional.of(new Empresa()));

        // Ejecuta el método del servicio
        Optional<Empresa> result = empresaService.buscarPorCorreoContacto("empresa@correo.com");

        // Verifica que el resultado esté presente
        assertTrue(result.isPresent());
        verify(empresaRepository, times(1)).findByCorreoContacto("empresa@correo.com");
    }

    @Test
    void buscarPorCorreoContacto_noExiste() {
        // Simula que el repositorio no encuentra ninguna empresa con el correo indicado
        when(empresaRepository.findByCorreoContacto("empresa@correo.com")).thenReturn(Optional.empty());

        // Ejecuta el método del servicio
        Optional<Empresa> result = empresaService.buscarPorCorreoContacto("empresa@correo.com");

        // Verifica que el resultado esté vacío
        assertFalse(result.isPresent());
        verify(empresaRepository, times(1)).findByCorreoContacto("empresa@correo.com");
    }

    @Test
    void existePorRuc_existe() {
        // Simula que el repositorio confirma que existe una empresa con el RUC indicado
        when(empresaRepository.existsByRuc("20123456789")).thenReturn(true);

        // Ejecuta el método del servicio y verifica que retorna true
        assertTrue(empresaService.existePorRuc("20123456789"));
        verify(empresaRepository, times(1)).existsByRuc("20123456789");
    }

    @Test
    void existePorRuc_noExiste() {
        // Simula que el repositorio confirma que no existe ninguna empresa con el RUC indicado
        when(empresaRepository.existsByRuc("20123456789")).thenReturn(false);

        // Ejecuta el método del servicio y verifica que retorna false
        assertFalse(empresaService.existePorRuc("20123456789"));
        verify(empresaRepository, times(1)).existsByRuc("20123456789");
    }

    @Test
    void existePorCorreoContacto_existe() {
        // Simula que el repositorio confirma que existe una empresa con el correo indicado
        when(empresaRepository.existsByCorreoContacto("empresa@correo.com")).thenReturn(true);

        // Ejecuta el método del servicio y verifica que retorna true
        assertTrue(empresaService.existePorCorreoContacto("empresa@correo.com"));
        verify(empresaRepository, times(1)).existsByCorreoContacto("empresa@correo.com");
    }

    @Test
    void existePorCorreoContacto_noExiste() {
        // Simula que el repositorio confirma que no existe ninguna empresa con el correo indicado
        when(empresaRepository.existsByCorreoContacto("empresa@correo.com")).thenReturn(false);

        // Ejecuta el método del servicio y verifica que retorna false
        assertFalse(empresaService.existePorCorreoContacto("empresa@correo.com"));
        verify(empresaRepository, times(1)).existsByCorreoContacto("empresa@correo.com");
    }
}