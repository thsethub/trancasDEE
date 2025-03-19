package br.dee.trancasdee.services;


import br.dee.trancasdee.models.Ambientes;
import br.dee.trancasdee.respositories.AmbientesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmbientesService {

    private final AmbientesRepository ambientesRepository;

    public AmbientesService(AmbientesRepository ambientesRepository) {
        this.ambientesRepository = ambientesRepository;
    }

    public List<Ambientes> findAll() {
        return ambientesRepository.findAll();
    }

    public Ambientes findById(Long id) {
        return ambientesRepository.findById(id).orElse(null);
    }

    public Ambientes save(Ambientes ambientes){
        return ambientesRepository.save(ambientes);
    }
}
