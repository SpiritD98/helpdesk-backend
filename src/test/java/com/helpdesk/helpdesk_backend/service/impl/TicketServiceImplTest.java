package com.helpdesk.helpdesk_backend.service.impl;

import com.helpdesk.helpdesk_backend.dto.CambiarEstadoRequestDTO;
import com.helpdesk.helpdesk_backend.dto.CalificacionRequestDTO;
import com.helpdesk.helpdesk_backend.dto.CierreRequestDTO;
import com.helpdesk.helpdesk_backend.exception.ResourceNotFoundException;
import com.helpdesk.helpdesk_backend.model.*;
import com.helpdesk.helpdesk_backend.model.enums.EstadoTicket;
import com.helpdesk.helpdesk_backend.model.enums.PrioridadTicket;
import com.helpdesk.helpdesk_backend.repository.*;
import com.helpdesk.helpdesk_backend.security.UsuarioPrincipal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EmpresaRepository empresaRepository;
    @Mock private CategoriaTicketRepository categoriaRepository;
    @Mock private ProblemaTicketRepository problemaRepository;
    @Mock private TicketComentarioRepository comentarioRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Ticket ticket;
    private Empresa empresa;
    private Usuario cliente;
    private Usuario agente;
    private CategoriaTicket categoria;
    private ProblemaTicket problema;

    @BeforeEach
    void setUp() {
        empresa = Empresa.builder().id(1L).nombre("Tech Solutions").build();
        cliente = Usuario.builder().id(1L).nombres("Juan").apellidos("Perez").email("juan@test.com").build();
        agente = Usuario.builder().id(2L).nombres("Maria").apellidos("Lopez").email("maria@test.com").build();
        categoria = CategoriaTicket.builder().id(1L).nombre("Soporte").empresa(empresa).build();
        problema = ProblemaTicket.builder().id(1L).nombre("Falla de red").categoria(categoria).build();

        ticket = Ticket.builder()
                .id(1L)
                .codigo("TCK-1001")
                .titulo("Problema de red")
                .descripcion("Sin conexión")
                .estado(EstadoTicket.ABIERTO)
                .prioridad(PrioridadTicket.ALTA)
                .cliente(cliente)
                .empresa(empresa)
                .build();

        // Simula un usuario autenticado (ADMIN_EMPRESA, empresa 1) en el SecurityContext.
        // guardar() deriva empresa/cliente del JWT firmado y ya NO del body.
        UsuarioPrincipal principal = new UsuarioPrincipal(
                1L, "juan@test.com", "pass", 1L, "Tech Solutions", "ADMIN_EMPRESA", true, List.of());
        Authentication auth = mock(Authentication.class);
        lenient().when(auth.isAuthenticated()).thenReturn(true);
        lenient().when(auth.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(new SecurityContextImpl(auth));

        // Mocks por defecto requeridos por guardar()/guardarConComentarioInicial()
        // (lenient: no todos los tests los usan).
        lenient().when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        lenient().when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void buscarPorId_debeRetornarTicket() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        Optional<Ticket> resultado = ticketService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("TCK-1001", resultado.get().getCodigo());
    }

    @Test
    void buscarPorId_noExiste_debeRetornarVacio() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Ticket> resultado = ticketService.buscarPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void guardar_debeAsignarCodigoYGuardar() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket nuevo = Ticket.builder()
                .titulo("Nuevo ticket")
                .descripcion("Descripción")
                .build();

        Ticket resultado = ticketService.guardar(nuevo);

        assertNotNull(resultado);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void guardar_sinEstadoNiPrioridad_debeAsignarDefecto() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket nuevo = Ticket.builder()
                .titulo("Ticket sin estado")
                .descripcion("desc")
                .build();

        ticketService.guardar(nuevo);

        assertEquals(EstadoTicket.ABIERTO, nuevo.getEstado());
        assertEquals(PrioridadTicket.MEDIA, nuevo.getPrioridad());
    }

    @Test
    void guardar_conEstadoYPrioridad_noSobreescribe() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket nuevo = Ticket.builder()
                .titulo("Ticket con estado")
                .descripcion("desc")
                .estado(EstadoTicket.EN_PROGRESO)
                .prioridad(PrioridadTicket.CRITICA)
                .build();

        ticketService.guardar(nuevo);

        assertEquals(EstadoTicket.EN_PROGRESO, nuevo.getEstado());
        assertEquals(PrioridadTicket.CRITICA, nuevo.getPrioridad());
    }

    @Test
    void guardar_empresaYClienteVienenDelJwt_noDelBody() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(problemaRepository.findById(1L)).thenReturn(Optional.of(problema));
        when(usuarioRepository.findByIdAndEmpresaId(2L, 1L)).thenReturn(Optional.of(agente));

        // El body intenta crear el ticket para OTRA empresa/cliente (10/99).
        // El servicio debe IGNORARLO y usar los del JWT (empresa 1, usuario 1).
        Ticket nuevo = Ticket.builder()
                .titulo("Ticket con refs")
                .descripcion("desc")
                .empresa(Empresa.builder().id(10L).build())
                .cliente(Usuario.builder().id(99L).build())
                .categoria(categoria)
                .problema(problema)
                .agenteAsignado(agente)
                .build();

        ticketService.guardar(nuevo);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(captor.capture());
        Ticket guardado = captor.getValue();
        assertEquals(1L, guardado.getEmpresa().getId());
        assertEquals(1L, guardado.getCliente().getId());
        assertEquals(categoria, guardado.getCategoria());
        assertEquals(problema, guardado.getProblema());
        assertEquals(agente, guardado.getAgenteAsignado());
    }

    @Test
    void guardarConComentarioInicial_conMensaje_debeGuardarComentario() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(comentarioRepository.save(any(TicketComentario.class))).thenReturn(null);

        Ticket resultado = ticketService.guardarConComentarioInicial(ticket, "Primer comentario", 1L);

        assertNotNull(resultado);
        verify(comentarioRepository).save(any(TicketComentario.class));
    }

    @Test
    void guardarConComentarioInicial_sinMensaje_noGuardaComentario() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        ticketService.guardarConComentarioInicial(ticket, "", 1L);

        verify(comentarioRepository, never()).save(any());
    }

    @Test
    void guardarConComentarioInicial_mensajeNull_noGuardaComentario() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        ticketService.guardarConComentarioInicial(ticket, null, 1L);

        verify(comentarioRepository, never()).save(any());
    }

    @Test
    void guardarConComentarioInicial_usuarioNoExiste_lanzaExcepcion() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                ticketService.guardarConComentarioInicial(ticket, "mensaje", 99L));
    }

    @Test
    void actualizar_debeActualizarYRetornarTicket() {
        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket actualizado = Ticket.builder()
                .titulo("Título actualizado")
                .descripcion("Nueva descripción")
                .prioridad(PrioridadTicket.ALTA)
                .build();

        Ticket resultado = ticketService.actualizar(1L, 1L, actualizado);

        assertNotNull(resultado);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void actualizar_enviaEstadoEnBody_seIgnora() {
        // Estado original ABIERTO
        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // El body intenta cambiar a CERRADO — debe ignorarse
        Ticket intento = Ticket.builder()
                .titulo("Nuevo título")
                .descripcion("Nueva desc")
                .estado(EstadoTicket.CERRADO)
                .build();

        ticketService.actualizar(1L, 1L, intento);

        // El estado NO se modifica
        assertEquals(EstadoTicket.ABIERTO, ticket.getEstado());
    }

    @Test
    void actualizar_noExiste_lanzaExcepcion() {
        when(ticketRepository.findByIdAndEmpresaId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                ticketService.actualizar(99L, 1L, ticket));
    }

    @Test
    void actualizar_sinEstadoNiPrioridad_noSobreescribe() {
        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket actualizado = Ticket.builder()
                .titulo("Título")
                .descripcion("desc")
                .build(); // sin estado ni prioridad

        ticketService.actualizar(1L, 1L, actualizado);

        assertEquals(EstadoTicket.ABIERTO, ticket.getEstado());
        assertEquals(PrioridadTicket.ALTA, ticket.getPrioridad());
    }

    @Test
    void actualizar_conReferencias_debeResolverlas() {
        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(usuarioRepository.getReferenceById(2L)).thenReturn(agente);
        when(categoriaRepository.getReferenceById(1L)).thenReturn(categoria);
        when(problemaRepository.getReferenceById(1L)).thenReturn(problema);

        Ticket actualizado = Ticket.builder()
                .titulo("Título")
                .descripcion("desc")
                .cliente(cliente)
                .agenteAsignado(agente)
                .empresa(empresa)
                .categoria(categoria)
                .problema(problema)
                .build();

        ticketService.actualizar(1L, 1L, actualizado);

        verify(usuarioRepository).getReferenceById(2L);
        verify(categoriaRepository).getReferenceById(1L);
        verify(problemaRepository).getReferenceById(1L);
        // empresa y cliente son inmutables en actualizar: no se re-resuelven
        verify(empresaRepository, never()).getReferenceById(anyLong());
        verify(usuarioRepository, never()).getReferenceById(1L);
    }

    @Test
    void eliminar_debeEliminarTicket() {
        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));

        ticketService.eliminar(1L, 1L);

        verify(comentarioRepository).deleteByTicketId(1L);
        verify(ticketRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExiste_lanzaExcepcion() {
        when(ticketRepository.findByIdAndEmpresaId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                ticketService.eliminar(99L, 1L));
    }

    @Test
    void buscarPorCodigo_debeRetornarTicket() {
        when(ticketRepository.findByCodigo("TCK-1001")).thenReturn(Optional.of(ticket));

        Optional<Ticket> resultado = ticketService.buscarPorCodigo("TCK-1001");

        assertTrue(resultado.isPresent());
    }

    @Test
    void listarPorEmpresaId_debeRetornarLista() {
        when(ticketRepository.findByEmpresaId(1L)).thenReturn(List.of(ticket));

        List<Ticket> resultado = ticketService.listarPorEmpresaId(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void listarPorClienteId_debeRetornarLista() {
        when(ticketRepository.findByClienteId(1L)).thenReturn(List.of(ticket));

        assertEquals(1, ticketService.listarPorClienteId(1L).size());
    }

    @Test
    void listarPorAgenteAsignadoId_debeRetornarLista() {
        when(ticketRepository.findByAgenteAsignadoId(1L)).thenReturn(List.of(ticket));

        assertEquals(1, ticketService.listarPorAgenteAsignadoId(1L).size());
    }

    @Test
    void listarPorCategoriaId_debeRetornarLista() {
        when(ticketRepository.findByCategoriaId(1L)).thenReturn(List.of(ticket));

        assertEquals(1, ticketService.listarPorCategoriaId(1L).size());
    }

    @Test
    void listarPorEstado_debeRetornarLista() {
        when(ticketRepository.findByEstado(EstadoTicket.ABIERTO)).thenReturn(List.of(ticket));

        assertEquals(1, ticketService.listarPorEstado(EstadoTicket.ABIERTO).size());
    }

    @Test
    void listarPorPrioridad_debeRetornarLista() {
        when(ticketRepository.findByPrioridad(PrioridadTicket.ALTA)).thenReturn(List.of(ticket));

        assertEquals(1, ticketService.listarPorPrioridad(PrioridadTicket.ALTA).size());
    }

    @Test
    void listarPorEmpresaIdYEstado_debeRetornarLista() {
        when(ticketRepository.findByEmpresaIdAndEstado(1L, EstadoTicket.ABIERTO))
                .thenReturn(List.of(ticket));

        assertEquals(1, ticketService.listarPorEmpresaIdYEstado(1L, EstadoTicket.ABIERTO).size());
    }

    @Test
    void listarPorEmpresaIdYPrioridad_debeRetornarLista() {
        when(ticketRepository.findByEmpresaIdAndPrioridad(1L, PrioridadTicket.ALTA))
                .thenReturn(List.of(ticket));

        assertEquals(1, ticketService.listarPorEmpresaIdYPrioridad(1L, PrioridadTicket.ALTA).size());
    }

    @Test
    void existePorCodigo_debeRetornarTrue() {
        when(ticketRepository.existsByCodigo("TCK-1001")).thenReturn(true);

        assertTrue(ticketService.existePorCodigo("TCK-1001"));
    }

    @Test
    void listarPorEmpresaConDetalles_debeRetornarLista() {
        when(ticketRepository.findByEmpresaConDetallesOrdenado(1L)).thenReturn(List.of(ticket));

        assertEquals(1, ticketService.listarPorEmpresaConDetalles(1L).size());
    }

    @Test
    void filtrarTickets_debeRetornarLista() {
        when(ticketRepository.filtrarTickets(1L, EstadoTicket.ABIERTO, PrioridadTicket.ALTA))
                .thenReturn(List.of(ticket));

        assertEquals(1, ticketService.filtrarTickets(1L, EstadoTicket.ABIERTO, PrioridadTicket.ALTA).size());
    }

    @Test
    void contarPorEstado_debeRetornarLista() {
        when(ticketRepository.contarPorEstado(1L)).thenReturn(List.of());

        assertNotNull(ticketService.contarPorEstado(1L));
    }

    @Test
    void listarPorAgenteYEstado_debeRetornarLista() {
        when(ticketRepository.findByAgenteYEstado(1L, EstadoTicket.ABIERTO))
                .thenReturn(List.of(ticket));

        assertEquals(1, ticketService.listarPorAgenteYEstado(1L, EstadoTicket.ABIERTO).size());
    }

    @Test
    void listarPorEmpresaYPeriodo_debeRetornarLista() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();

        when(ticketRepository.findByEmpresaYPeriodo(1L, inicio, fin)).thenReturn(List.of(ticket));

        assertEquals(1, ticketService.listarPorEmpresaYPeriodo(1L, inicio, fin).size());
    }

    @Test
    void listarPrioridadAltaPorEmpresa_debeRetornarLista() {
        when(ticketRepository.findPrioridadAltaPorEmpresa(1L)).thenReturn(List.of(ticket));

        assertEquals(1, ticketService.listarPrioridadAltaPorEmpresa(1L).size());
    }

    // ── Tests de la máquina de estados (A5) ──

    @Test
    void cambiarEstado_transicionValida_AbiertoAEnProgreso_debeCambiar() {
        CambiarEstadoRequestDTO request = new CambiarEstadoRequestDTO();
        request.setEstado(EstadoTicket.EN_PROGRESO);

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket resultado = ticketService.cambiarEstado(1L, 1L, request);

        assertEquals(EstadoTicket.EN_PROGRESO, resultado.getEstado());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void cambiarEstado_transicionInvalida_CerradoAAbierto_lanzaExcepcion() {
        ticket.setEstado(EstadoTicket.CERRADO);

        CambiarEstadoRequestDTO request = new CambiarEstadoRequestDTO();
        request.setEstado(EstadoTicket.ABIERTO);

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.cambiarEstado(1L, 1L, request));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void cambiarEstado_transicionInvalida_ResueltoAAbierto_lanzaExcepcion() {
        ticket.setEstado(EstadoTicket.RESUELTO);

        CambiarEstadoRequestDTO request = new CambiarEstadoRequestDTO();
        request.setEstado(EstadoTicket.ABIERTO);

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.cambiarEstado(1L, 1L, request));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void cambiarEstado_mismoEstado_sePermiteNoOp() {
        // ticket ya está ABIERTO, request pide ABIERTO
        CambiarEstadoRequestDTO request = new CambiarEstadoRequestDTO();
        request.setEstado(EstadoTicket.ABIERTO);

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        assertDoesNotThrow(() -> ticketService.cambiarEstado(1L, 1L, request));
        verify(ticketRepository).save(any(Ticket.class));
        // No genera comentario del sistema en no-op
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    void cambiarEstado_cerradoRequiereJustificacion() {
        CambiarEstadoRequestDTO request = new CambiarEstadoRequestDTO();
        request.setEstado(EstadoTicket.CERRADO);
        // sin justificacionCierre

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.cambiarEstado(1L, 1L, request));
    }

    @Test
    void guardarCierre_desdeAbierto_debeFuncionar() {
        ticket.setEstado(EstadoTicket.ABIERTO);

        CierreRequestDTO request = new CierreRequestDTO();
        request.setJustificacionCierre("Resuelto correctamente");
        request.setUsuarioId(1L);

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket resultado = ticketService.guardarCierre(1L, 1L, request);

        assertEquals(EstadoTicket.RESUELTO, resultado.getEstado());
    }

    @Test
    void guardarCierre_desdeCerrado_lanzaExcepcion() {
        ticket.setEstado(EstadoTicket.CERRADO);

        CierreRequestDTO request = new CierreRequestDTO();
        request.setJustificacionCierre("Intento de cierre");
        request.setUsuarioId(1L);

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.guardarCierre(1L, 1L, request));

        verify(ticketRepository, never()).save(any());
    }

    // ─── Flujo canónico: agente resuelve → cliente califica → ticket se CIERRA ───

    @Test
    void calificarTicket_resuelto_calificaYCierraElTicket() {
        ticket.setEstado(EstadoTicket.RESUELTO);
        ticket.setAgenteAsignado(agente); // agente asignado es requerido para calificar

        // El principal simulado tiene id=1, que coincide con el cliente del ticket.
        CalificacionRequestDTO request = new CalificacionRequestDTO();
        request.setCalificacion(5);

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioRepository.getReferenceById(1L)).thenReturn(cliente);

        Ticket resultado = ticketService.calificarTicket(1L, 1L, request);

        assertEquals(Integer.valueOf(5), resultado.getCalificacionAgente());
        assertEquals(EstadoTicket.CERRADO, resultado.getEstado(),
                "Calificar un ticket RESUELTO debe cerrarlo automáticamente.");
        verify(comentarioRepository).save(any());
    }

    @Test
    void calificarTicket_otroCliente_lanzaExcepcion() {
        ticket.setEstado(EstadoTicket.RESUELTO);
        ticket.setAgenteAsignado(agente);

        // El principal simulado tiene id=1; le ponemos un cliente distinto al ticket.
        ticket.setCliente(Usuario.builder().id(99L).build());

        CalificacionRequestDTO request = new CalificacionRequestDTO();
        request.setCalificacion(4);

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.calificarTicket(1L, 1L, request));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void calificarTicket_yaCalificado_lanzaExcepcion() {
        ticket.setEstado(EstadoTicket.RESUELTO);
        ticket.setAgenteAsignado(agente);
        ticket.setCalificacionAgente(3); // ya calificado

        CalificacionRequestDTO request = new CalificacionRequestDTO();
        request.setCalificacion(5);

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.calificarTicket(1L, 1L, request));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void calificarTicket_noResuelto_lanzaExcepcion() {
        ticket.setEstado(EstadoTicket.EN_PROGRESO);
        ticket.setAgenteAsignado(agente);

        CalificacionRequestDTO request = new CalificacionRequestDTO();
        request.setCalificacion(4);

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.calificarTicket(1L, 1L, request));
        verify(ticketRepository, never()).save(any());
    }

    // ─── Reapertura: cliente reabre un ticket RESUELTO → vuelve a EN_PROGRESO ───

    @Test
    void reabrirTicket_resuelto_vuelveAEnProgreso() {
        ticket.setEstado(EstadoTicket.RESUELTO);
        // cliente del ticket = id=1 (mismo que el principal simulado)

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioRepository.getReferenceById(1L)).thenReturn(cliente);

        Ticket resultado = ticketService.reabrirTicket(1L, 1L);

        assertEquals(EstadoTicket.EN_PROGRESO, resultado.getEstado(),
                "Reabrir un ticket RESUELTO debe devolverlo a EN_PROGRESO.");
        verify(comentarioRepository).save(any());
    }

    @Test
    void reabrirTicket_otroCliente_lanzaExcepcion() {
        ticket.setEstado(EstadoTicket.RESUELTO);
        ticket.setCliente(Usuario.builder().id(99L).build()); // otro cliente

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.reabrirTicket(1L, 1L));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void reabrirTicket_cerrado_lanzaExcepcion() {
        ticket.setEstado(EstadoTicket.CERRADO); // no se puede reabrir un CERRADO

        when(ticketRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.reabrirTicket(1L, 1L));
        verify(ticketRepository, never()).save(any());
    }

    // ─── Auto-cierre: tickets RESUELTO con >7 días se cierran solos ───

    @Test
    void cerrarResueltosAutomaticamente_cierraResueltosAntiguos() {
        Ticket viejo = Ticket.builder()
                .id(2L).estado(EstadoTicket.RESUELTO)
                .empresa(empresa).cliente(cliente).build();
        Ticket reciente = Ticket.builder()
                .id(3L).estado(EstadoTicket.RESUELTO)
                .empresa(empresa).cliente(cliente).build();

        // Simulamos que la query devuelve uno antiguo y uno reciente; el service
        // cierra TODOS los que devuelve la query (el filtro por fecha ya lo hizo la BD).
        when(ticketRepository.findResueltosAntesDe(any(LocalDateTime.class)))
                .thenReturn(List.of(viejo));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        int cerrados = ticketService.cerrarResueltosAutomaticamente();

        assertEquals(1, cerrados);
        assertEquals(EstadoTicket.CERRADO, viejo.getEstado());
        verify(comentarioRepository).save(any());
    }

    @Test
    void cerrarResueltosAutomaticamente_sinResueltosAntiguos_cero() {
        when(ticketRepository.findResueltosAntesDe(any(LocalDateTime.class)))
                .thenReturn(List.of());

        int cerrados = ticketService.cerrarResueltosAutomaticamente();

        assertEquals(0, cerrados);
        verify(ticketRepository, never()).save(any());
        verify(comentarioRepository, never()).save(any());
    }
}