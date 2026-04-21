package com.helpdesk.helpdesk_backend.controller;

import com.helpdesk.helpdesk_backend.model.CategoriaTicket;
import com.helpdesk.helpdesk_backend.service.CategoriaTicketService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaTicketControllerTest {
    //Creamos la simulación del servicio para el controlador
    @Mock
    private CategoriaTicketService categoriaService;
    //Inyectamos el mock en el controlador
    @InjectMocks
    private CategoriaTicketController controller;
    //Inicializamos los mocks
    public CategoriaTicketControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarTodos() {
        //Creamos una lista falsa de 1 categoría
        List<CategoriaTicket> lista = List.of(new CategoriaTicket());
        when(categoriaService.listarTodas()).thenReturn(lista);//devuelve la lista cuando el servicio sea llamado
        //Ejecutamos el método del controlador
        ResponseEntity<List<CategoriaTicket>> response = controller.listarTodos();
        //Verificamos que responde 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());//Validamos que no sea null
        assertEquals(1, response.getBody().size());//Validamos que haya un elemento
    }

    @Test
    void buscarPorId_existente() {
        CategoriaTicket cat = new CategoriaTicket();//Creamos objeto de prueba
        //Simulamos que existe
        when(categoriaService.buscarPorId(1L)).thenReturn(Optional.of(cat));
        //Ejecutamos el metodo
        ResponseEntity<CategoriaTicket> response = controller.buscarPorId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());//Debe devolver 200
        assertNotNull(response.getBody());//El cuerpo debe existir
    }

    @Test
    void buscarPorId_noExiste() {
        //Simulamos que no existe
        when(categoriaService.buscarPorId(1L)).thenReturn(Optional.empty());
        ResponseEntity<CategoriaTicket> response = controller.buscarPorId(1L); //Ejecutamos
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); //Debe devolver 404 si no existe
    }

    @Test
    void guardar() {
        CategoriaTicket cat = new CategoriaTicket();//Se crea objeto
        when(categoriaService.guardar(cat)).thenReturn(cat);//Simulamos guardado
        ResponseEntity<CategoriaTicket> response = controller.guardar(cat);//Ejecutamos
        assertEquals(HttpStatus.CREATED, response.getStatusCode());//Debe devolver 201
        assertNotNull(response.getBody());//Debe tener contenido
    }

    @Test
    void eliminar() {
        //Simulamos que elimina errores
        doNothing().when(categoriaService).eliminar(1L);
        //Ejecutamos el método
        ResponseEntity<Void> response = controller.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Debe devolver 204
        //Verificamos que el método se haya ejecutado 1 vez
        verify(categoriaService, times(1)).eliminar(1L);
    }
}