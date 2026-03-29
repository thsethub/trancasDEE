package br.dee.trancasdee.services;


import br.dee.trancasdee.models.Usuarios.Usuarios;
import br.dee.trancasdee.respositories.UsuariosRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuariosService {

    private final UsuariosRepository usuariosRepository;
    private final ModelMapper modelMapper;

    public UsuariosService(UsuariosRepository usuariosRepository, ModelMapper modelMapper) {
        this.usuariosRepository = usuariosRepository;
        this.modelMapper = modelMapper;
    }

    public Usuarios updateUniqueID(Long id, Long uniqueID) {
        return usuariosRepository.findById(id).map(usuario -> {
            usuario.setUniqueID(uniqueID);
            return usuariosRepository.save(usuario);
        }).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }


    public List<Usuarios> findAll() {
        return usuariosRepository.findAll();
    }

    public List<Usuarios> buscaPorNome(String nome) {
        return usuariosRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Usuarios findById(Long id) {
        return usuariosRepository.findById(id).orElse(null);
    }

    public Page<Usuarios> findDisponiveisBySala(String nome, Long salaId, Pageable pageable) {
        return usuariosRepository.findDisponiveisBySalaId(nome, salaId, pageable);
    }

    public Usuarios save(Usuarios usuarios) {
        return usuariosRepository.save(usuarios);
    }

    public void delete(Long id) {
        usuariosRepository.deleteById(id);
    }
}
