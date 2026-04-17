package com.helpdesk.helpdesk_backend.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.helpdesk.helpdesk_backend.model.Empresa;
import com.helpdesk.helpdesk_backend.repository.EmpresaRepository;
import com.helpdesk.helpdesk_backend.service.EmpresaService;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaServiceImpl(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    /**
     * Lista todas las empresas registradas.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Empresa> listarTodos() {
        return empresaRepository.findAll();
    }

    /**
     * Busca una empresa por su ID.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> buscarPorId(Long id) {
        return empresaRepository.findById(id);
    }

    /**
     * Guarda una nueva empresa validando el RUC y Correo.
     */
    @Override
    public Empresa guardar(Empresa empresa) {
        if (empresaRepository.existsByRuc(empresa.getRuc())) {
            throw new RuntimeException("Error: Ya existe una empresa con el RUC ingresado.");
        }
        if (empresaRepository.existsByCorreoContacto(empresa.getCorreoContacto())) {
            throw new RuntimeException("Error: Ya existe una empresa con el correo de contacto ingresado.");
        }
        return empresaRepository.save(empresa);
    }

    /**
     * Actualiza una empresa existente.
     */
    @Override
    public Empresa actualizar(Long id, Empresa empresa) {
        Empresa empresaExistente = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Empresa no encontrada con el id " + id));

        // Validaciones si se cambia el RUC o Correo
        if (!empresaExistente.getRuc().equals(empresa.getRuc()) && empresaRepository.existsByRuc(empresa.getRuc())) {
            throw new RuntimeException("Error: Ya existe otra empresa con el RUC ingresado.");
        }
        if (!empresaExistente.getCorreoContacto().equals(empresa.getCorreoContacto()) && empresaRepository.existsByCorreoContacto(empresa.getCorreoContacto())) {
            throw new RuntimeException("Error: Ya existe otra empresa con el correo de contacto ingresado.");
        }

        empresaExistente.setNombre(empresa.getNombre());
        empresaExistente.setRuc(empresa.getRuc());
        empresaExistente.setCorreoContacto(empresa.getCorreoContacto());
        empresaExistente.setTelefonoContacto(empresa.getTelefonoContacto());
        empresaExistente.setActivo(empresa.isActivo());

        return empresaRepository.save(empresaExistente);
    }

    /**
     * Elimina una empresa por su ID.
     */
    @Override
    public void eliminar(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new RuntimeException("Error: No se puede eliminar. Empresa no encontrada con el id " + id);
        }
        empresaRepository.deleteById(id);
    }

    /**
     * Busca una empresa por su RUC.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> buscarPorRuc(String ruc) {
        return empresaRepository.findByRuc(ruc);
    }

    /**
     * Busca una empresa por el correo de contacto.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> buscarPorCorreoContacto(String correoContacto) {
        return empresaRepository.findByCorreoContacto(correoContacto);
    }

    /**
     * Verifica si una empresa existe a través de su RUC.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existePorRuc(String ruc) {
        return empresaRepository.existsByRuc(ruc);
    }

    /**
     * Verifica si una empresa existe a través de su correo de contacto.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existePorCorreoContacto(String correoContacto) {
        return empresaRepository.existsByCorreoContacto(correoContacto);
    }
}