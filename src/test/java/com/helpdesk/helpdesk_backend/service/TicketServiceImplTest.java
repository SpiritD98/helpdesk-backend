package com.helpdesk.helpdesk_backend.service;

import com.helpdesk.helpdesk_backend.model.CategoriaTicket;
import com.helpdesk.helpdesk_backend.model.Empresa;
import com.helpdesk.helpdesk_backend.model.Ticket;
import com.helpdesk.helpdesk_backend.model.Usuario;
import com.helpdesk.helpdesk_backend.model.enums.EstadoTicket;
import com.helpdesk.helpdesk_backend.model.enums.PrioridadTicket;
import com.helpdesk.helpdesk_backend.repository.CategoriaTicketRepository;
import com.helpdesk.helpdesk_backend.repository.EmpresaRepository;
import com.helpdesk.helpdesk_backend.repository.TicketRepository;
import com.helpdesk.helpdesk_backend.repository.UsuarioRepository;
import com.helpdesk.helpdesk_backend.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// Usamos la anotación moderna de JUnit 5 en lugar del constructor con openMocks
@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    // 1. Añadimos TODOS los repositorios que usa el Service
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private CategoriaTicketRepository categoriaTicketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Test
    void guardar_generaCodigo() {
        Ticket ticket = new Ticket();
        ticket.setCodigo(null);//Simulamos que el ticket viene sin código
        //Decimos que el código no existe
        when(ticketRepository.existsByCodigo(anyString())).thenReturn(false);
        //Simulamos que al guardar, el repositorio devuelve el mismo ticket con un ID asignado
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArguments()[0]);
        Ticket resultado = ticketService.guardar(ticket);
        //Verificamos que el sistema generó un código automáticamente
        assertNotNull(resultado.getCodigo());
        verify(ticketRepository).save(ticket);//Confirmamos que se guardó
    }

    @Test
    void guardar_codigoDuplicado() {
        Ticket ticket = new Ticket();
        ticket.setCodigo("TCK-1234");
        //Simulamos que el código ya existe en la base de datos
        when(ticketRepository.existsByCodigo("TCK-1234")).thenReturn(true);
        //Verificamos que lanza error
        RuntimeException ex = assertThrows(RuntimeException.class,() -> ticketService.guardar(ticket));
        //Validamos el mensaje
        assertTrue(ex.getMessage().contains("Ya existe"));
    }

    @Test
    void guardar_conRelacionesCompletas(){
        // Arrange: Creamos un ticket con TODAS sus relaciones para entrar a los 'if'
        Ticket ticket = new Ticket();
        ticket.setCodigo("TCK-EXISTENTE"); // Le ponemos código para probar la rama donde no se genera uno nuevo

        Usuario cliente = new Usuario(); cliente.setId(1L);
        Empresa empresa = new Empresa(); empresa.setId(1L);
        CategoriaTicket categoria = new CategoriaTicket(); categoria.setId(1L);
        Usuario agente = new Usuario(); agente.setId(2L);

        ticket.setCliente(cliente);
        ticket.setEmpresa(empresa);
        ticket.setCategoria(categoria);
        ticket.setAgenteAsignado(agente);

        // Simulamos el comportamiento de los repositorios extra para que el servicio pueda validar las relaciones
        when(usuarioRepository.getReferenceById(1L)).thenReturn(cliente);
        when(empresaRepository.getReferenceById(1L)).thenReturn(empresa);
        when(categoriaTicketRepository.getReferenceById(1L)).thenReturn(categoria);
        when(usuarioRepository.getReferenceById(2L)).thenReturn(agente);
        when(ticketRepository.existsByCodigo("TCK-EXISTENTE")).thenReturn(false);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // Act: Guardamos el ticket
        Ticket resultado = ticketService.guardar(ticket);

        // Assert: Verificamos que se guardó correctamente con todas las relaciones
        assertNotNull(resultado); // El código no debió cambiar
        assertEquals("TCK-EXISTENTE", resultado.getCodigo());
        verify(usuarioRepository, times(1)).getReferenceById(1L);
        verify(usuarioRepository, times(1)).getReferenceById(2L);
    }

    @Test
    void guardar_conRelacionesPresentesPeroIdNulo_yCodigoVacio(){
        //Arrage: Creamos un ticket con relaciones pero sin IDs para probar la rama donde se generan códigos y se validan relaciones
        Ticket ticket = new Ticket();

        ticket.setCliente(new Usuario()); // Objeto instanciado, pero su ID es null
        ticket.setEmpresa(new Empresa()); // Objeto instanciado, pero su ID es null
        ticket.setCategoria(new CategoriaTicket()); // Objeto instanciado, pero su ID es null
        ticket.setAgenteAsignado(new Usuario()); // Objeto instanciado, pero su ID es null

        // Cubrimos la rama donde el código no es null, pero está vacío (la segunda parte del ||)
        ticket.setCodigo("");

        // Simulamos la base de datos
        when(ticketRepository.existsByCodigo(anyString())).thenReturn(false);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act: Guardamos el ticket
        Ticket resultado = ticketService.guardar(ticket);

        //Assert: Verificamos que se generó un código y se validaron las relaciones aunque los IDs fueran nulos
        assertNotNull(resultado.getCodigo());
        assertFalse(resultado.getCodigo().isEmpty()); // Verificamos que ya no está vacío

        // Verificamos que se intentó validar las relaciones, aunque los IDs fueran nulos (esto depende de cómo maneje el servicio los objetos sin ID, pero al menos confirmamos que se llamó a los repositorios)
        // Verificamos que NUNCA se llamaron a los repositorios secundarios porque los IDs eran nulos
        verify(usuarioRepository, never()).getReferenceById(anyLong());
        verify(empresaRepository, never()).getReferenceById(anyLong());
        verify(categoriaTicketRepository, never()).getReferenceById(anyLong());
    }

    @Test
    void actualizar_exitosoConEstadoYPrioridad() {
        Ticket ticketExistente = new Ticket();
        ticketExistente.setId(1L);

        Ticket ticketNuevosDatos = new Ticket();
        ticketNuevosDatos.setTitulo("Nuevo Titulo");
        ticketNuevosDatos.setDescripcion("Nueva descripcion");
        ticketNuevosDatos.setEstado(EstadoTicket.EN_PROGRESO);
        ticketNuevosDatos.setPrioridad(PrioridadTicket.ALTA);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketExistente));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticketNuevosDatos);

        Ticket resultado = ticketService.actualizar(1L, ticketNuevosDatos);

        assertEquals("Nuevo Titulo", resultado.getTitulo());
        assertEquals(EstadoTicket.EN_PROGRESO, resultado.getEstado());
        assertEquals(PrioridadTicket.ALTA, resultado.getPrioridad());
    }

    @Test
    void actualizar_exitosoSinEstadoNiPrioridad(){
        // Este test es vital para cubrir la rama donde el estado y prioridad vienen nulos (los if de actualizar)
        Ticket ticketExistente = new Ticket();
        ticketExistente.setId(1L);
        ticketExistente.setEstado(EstadoTicket.ABIERTO);
        ticketExistente.setPrioridad(PrioridadTicket.MEDIA);

        Ticket ticketNuevosDatos = new Ticket(); // Todo nulo
        ticketNuevosDatos.setTitulo("Solo cambio titulo");

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketExistente));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticketExistente);

        Ticket resultado = ticketService.actualizar(1L, ticketNuevosDatos);

        assertEquals("Solo cambio titulo", resultado.getTitulo());
        assertEquals(EstadoTicket.ABIERTO, resultado.getEstado()); // Mantiene el original
    }
    
    @Test
    void actualizar_noEncontrado(){
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> ticketService.actualizar(1L, new Ticket()));
    }

    @Test
    void buscarPorId_existente() {
        Ticket ticket = new Ticket();
        //Simulamos que el ticket existe
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        Optional<Ticket> resultado = ticketService.buscarPorId(1L);
        //Verificamos que encontró el ticket
        assertTrue(resultado.isPresent());
    }

    @Test
    void buscarPorCodigo(){
        Ticket ticket = new Ticket();
        //Simulamos que el ticket existe
        when(ticketRepository.findByCodigo("TCK-1234")).thenReturn(Optional.of(ticket));
        Optional<Ticket> resultado = ticketService.buscarPorCodigo("TCK-1234");
        //Verificamos que encontró el ticket
        assertTrue(resultado.isPresent());
    }

    @Test
    void existePorCodigo() {
        when(ticketRepository.existsByCodigo("TCK-1")).thenReturn(true);
        assertTrue(ticketService.existePorCodigo("TCK-1"));
    }

    @Test
    void eliminar_noExiste() {
        //Simulamos que el ticket no existe
        when(ticketRepository.existsById(1L)).thenReturn(false);
        //Verificamos que lanza error al intentar eliminar un ticket que no existe
        RuntimeException ex = assertThrows(RuntimeException.class,() -> ticketService.eliminar(1L));
        assertTrue(ex.getMessage().contains("no encontrado"));
    }

    @Test 
    void eliminar_exitoso(){
        //Simulamos que el ticket existe
        when(ticketRepository.existsById(1L)).thenReturn(true);
        //Llamamos al método eliminar
        ticketService.eliminar(1L);
        //Verificamos que se llamó al repositorio para eliminar
        verify(ticketRepository, times(1)).deleteById(1L);
    }

    @Test
    void listarTodos() {
        // Llamamos al método listarTodos
        ticketService.listarTodos();
        // Verificamos que se llamó al repositorio para listar todos los tickets
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void listarPorEmpresaId() {
        when(ticketRepository.findByEmpresaId(1L)).thenReturn(List.of(new Ticket()));
        assertFalse(ticketService.listarPorEmpresaId(1L).isEmpty());
    }

    @Test
    void listarPorClienteId() {
        when(ticketRepository.findByClienteId(1L)).thenReturn(List.of(new Ticket()));
        assertFalse(ticketService.listarPorClienteId(1L).isEmpty());
    }

    @Test
    void listarPorAgenteAsignadoId() {
        when(ticketRepository.findByAgenteAsignadoId(1L)).thenReturn(List.of(new Ticket()));
        assertFalse(ticketService.listarPorAgenteAsignadoId(1L).isEmpty());
    }

    @Test
    void listarPorCategoriaId() {
        when(ticketRepository.findByCategoriaId(1L)).thenReturn(List.of(new Ticket()));
        assertFalse(ticketService.listarPorCategoriaId(1L).isEmpty());
    }

    @Test
    void listarPorEstado() {
        when(ticketRepository.findByEstado(EstadoTicket.ABIERTO)).thenReturn(List.of(new Ticket()));
        assertFalse(ticketService.listarPorEstado(EstadoTicket.ABIERTO).isEmpty());
    }

    @Test
    void listarPorPrioridad() {
        when(ticketRepository.findByPrioridad(PrioridadTicket.ALTA)).thenReturn(List.of(new Ticket()));
        assertFalse(ticketService.listarPorPrioridad(PrioridadTicket.ALTA).isEmpty());
    }

    @Test
    void listarPorEmpresaIdYEstado() {
        when(ticketRepository.findByEmpresaIdAndEstado(1L, EstadoTicket.ABIERTO)).thenReturn(List.of(new Ticket()));
        assertFalse(ticketService.listarPorEmpresaIdYEstado(1L, EstadoTicket.ABIERTO).isEmpty());
    }

    @Test
    void listarPorEmpresaIdYPrioridad() {
        when(ticketRepository.findByEmpresaIdAndPrioridad(1L, PrioridadTicket.ALTA)).thenReturn(List.of(new Ticket()));
        assertFalse(ticketService.listarPorEmpresaIdYPrioridad(1L, PrioridadTicket.ALTA).isEmpty());
    }
}