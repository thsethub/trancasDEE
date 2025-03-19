package br.dee.trancasdee.services;

import br.dee.trancasdee.models.Acesso.Acesso;
import br.dee.trancasdee.models.Ambientes;
import br.dee.trancasdee.models.Usuarios.Usuarios;
import br.dee.trancasdee.respositories.AcessoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcessoService {

    private final AcessoRepository acessoRepository;

    public AcessoService(AcessoRepository acessoRepository) {
        this.acessoRepository = acessoRepository;
    }

    public List<Acesso> findAll() {
        return acessoRepository.findAll();
    }

    public Acesso findById(Long id) {
        return acessoRepository.findById(id).orElse(null);
    }

    public List<Acesso> findAcessoByUsuarios(Usuarios usuario) {
        return acessoRepository.findAcessoByUsuarios(usuario);
    }

    public List<Acesso> findAcessoByAmbientes(Ambientes ambientes) {
        return acessoRepository.findAcessoByAmbientes(ambientes);
    }

    public Acesso save(Acesso acesso) {
        return acessoRepository.save(acesso);
    }

    public AcessoRepository getAcessoRepository() {
        return acessoRepository;
    }

    public void delete(Long id) {
        acessoRepository.deleteById(id);
    }

}
