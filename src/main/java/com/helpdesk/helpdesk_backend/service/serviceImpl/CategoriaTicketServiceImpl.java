package com.helpdesk.helpdesk_backend.service.serviceImpl;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.helpdesk.helpdesk_backend.model.CategoriaTicket;
import com.helpdesk.helpdesk_backend.repository.CategoriaTicketRepository;
import lombok.RequiredArgsConstructor;
import com.helpdesk.helpdesk_backend.service.CategoriaTicketService;

@Service
@RequiredArgsConstructor
public class CategoriaTicketServiceImpl implements CategoriaTicketService {
    //Acceso a la bd
    private final CategoriaTicketRepository categoriaRepository;
    //Método crear
    @Override
    public CategoriaTicket crear(CategoriaTicket categoria) {
        // Validar duplicado por empresa
        if (categoriaRepository.existsByNombreAndEmpresaId(
                categoria.getNombre(), 
                categoria.getEmpresa().getId())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre en la empresa");
        }
        return categoriaRepository.save(categoria);
    }
    //Método listar
    @Override
    public List<CategoriaTicket> listarTodas() {
        return categoriaRepository.findAll();
    }
    //Método obtener por id
    @Override
    public Optional<CategoriaTicket> obtenerPorId(Long id) {
        return categoriaRepository.findById(id);
    }
    //Método actualizar
    @Override
    public CategoriaTicket actualizar(Long id, CategoriaTicket categoria) {
        CategoriaTicket existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        existente.setNombre(categoria.getNombre());
        existente.setDescripcion(categoria.getDescripcion());
        return categoriaRepository.save(existente);
    }
    //Método desactivar (simula una eliminación pero solo se desactiva)
    @Override
    public void eliminarLogico(Long id) {
        CategoriaTicket categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        categoria.setActiva(false); // eliminación lógica
        categoriaRepository.save(categoria);
    }

    //Filtros
    @Override
    public List<CategoriaTicket> listarPorEmpresa(Long empresaId) {
        return categoriaRepository.findByEmpresaId(empresaId);
    }
    @Override
    public List<CategoriaTicket> listarActivas(boolean activa) {
        return categoriaRepository.findByActiva(activa);
    }
    @Override
    public List<CategoriaTicket> listarPorEmpresaYActiva(Long empresaId, boolean activa) {
        return categoriaRepository.findByEmpresaIdAndActiva(empresaId, activa);
    }
}