package com.helpdesk.helpdesk_backend.service;

import com.helpdesk.helpdesk_backend.model.CategoriaTicket;
import com.helpdesk.helpdesk_backend.model.Empresa;
import com.helpdesk.helpdesk_backend.repository.CategoriaTicketRepository;
import com.helpdesk.helpdesk_backend.service.impl.CategoriaTicketServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaTicketServiceImplTest {
    //Simulamos la base de datos
    @Mock
    private CategoriaTicketRepository repository;
    //Inyectamos el mock en el servicio
    @InjectMocks
    private CategoriaTicketServiceImpl service;
    //Inicializamos los mocks antes de cada prueba
    public CategoriaTicketServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void guardar_ok() {
        CategoriaTicket cat = new CategoriaTicket();//Creamos una categoria 
        Empresa empresa = new Empresa();//Creamos una empresa
        empresa.setId(1L);
        //Asignamos valores
        cat.setEmpresa(empresa);
        cat.setNombre("Soporte");
        //Simulamos que no existe duplicados
        when(repository.existsByNombreAndEmpresaId("Soporte", 1L)).thenReturn(false);
        when(repository.save(cat)).thenReturn(cat);//Guardado exitoso
        //Ejecutamos lógica real del service
        CategoriaTicket resultado = service.guardar(cat);
        //Validamos que se guardó correctamente
        assertEquals("Soporte", resultado.getNombre());
        verify(repository).save(cat);//Verificamos que si llamo al repositorio
    }

    @Test
    void guardar_duplicado() {
        CategoriaTicket cat = new CategoriaTicket();//Creamos categoria
        Empresa empresa = new Empresa();//Creamos empresa
        empresa.setId(1L);
        cat.setEmpresa(empresa);
        cat.setNombre("Soporte");
        //Simulamos duplicados
        when(repository.existsByNombreAndEmpresaId("Soporte", 1L)).thenReturn(true);
        //Validamos que lanza una excepción por duplicado
        assertThrows(IllegalArgumentException.class, () -> service.guardar(cat));
    }

    @Test
    void buscarPorId() {
        //Simulamos que existe una categoria 
        CategoriaTicket cat = new CategoriaTicket();
        when(repository.findById(1L)).thenReturn(Optional.of(cat));
        //Ejecutamos lógica real del service
        Optional<CategoriaTicket> resultado = service.buscarPorId(1L);
        assertTrue(resultado.isPresent());//Validamos que encontró el dato
    }

    @Test
    void eliminar_noExiste() {
        //Simulamos que no existe
        when(repository.existsById(1L)).thenReturn(false);
        //Validamos que lanza un error
        assertThrows(RuntimeException.class, () -> service.eliminar(1L));
    }
}