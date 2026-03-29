package br.dee.trancasdee.services;

import br.dee.trancasdee.models.Acesso.Acesso;
import br.dee.trancasdee.models.Acesso.AcessoRequest;
import br.dee.trancasdee.models.Acesso.AcessoUpdateRequest;
import br.dee.trancasdee.models.Ambientes;
import br.dee.trancasdee.models.Usuarios.Usuarios;
import br.dee.trancasdee.respositories.AcessoRepository;
import br.dee.trancasdee.respositories.AmbientesRepository;
import br.dee.trancasdee.respositories.UsuariosRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class AcessoService {

    private final AcessoRepository acessoRepository;
    private final AmbientesRepository ambientesRepository;
    private final UsuariosRepository usuariosRepository;

    public AcessoService(AcessoRepository acessoRepository,
                         AmbientesRepository ambientesRepository,
                         UsuariosRepository usuariosRepository) {
        this.acessoRepository = acessoRepository;
        this.ambientesRepository = ambientesRepository;
        this.usuariosRepository = usuariosRepository;
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

    public List<Acesso> findAcessoByUniqueID(Long uniqueID) {
        return acessoRepository.findAcessoByUniqueID(uniqueID);
    }

    public List<Acesso> findAcessoBySalaAndUniqueID(Long sala, String uniqueID) {
        return acessoRepository.findAcessoBySalaAndUniqueID(sala, uniqueID);
    }

    public List<Acesso> findAcessoBySalaId(Long salaId) {
        return acessoRepository.findAcessoBySalaId(salaId);
    }

    public Page<Acesso> findAcessoBySalaIdPaged(Long salaId, String nome, Pageable pageable) {
        return acessoRepository.findAcessoBySalaIdPaged(salaId, nome == null ? "" : nome, pageable);
    }

    public Acesso update(Long id, AcessoUpdateRequest request) {
        Acesso acesso = acessoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Acesso não encontrado: " + id));
        ZoneId zoneRecife = ZoneId.of("America/Recife");
        LocalDate dataLimite = LocalDate.parse(request.dataLimite());
        acesso.setDataLimite(dataLimite.atStartOfDay(zoneRecife).toInstant());
        acesso.setHoraAcessoInicial(LocalTime.parse(request.horaAcessoInicial()));
        acesso.setHoraAcessoFinal(LocalTime.parse(request.horaAcessoFinal()));
        return acessoRepository.save(acesso);
    }

    public Acesso create(AcessoRequest request) {
        Ambientes sala = ambientesRepository.findById(request.salaId())
                .orElseThrow(() -> new RuntimeException("Sala não encontrada: " + request.salaId()));
        Usuarios usuario = usuariosRepository.findById(request.cpf())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + request.cpf()));

        ZoneId zoneRecife = ZoneId.of("America/Recife");
        LocalDate dataLimite = LocalDate.parse(request.dataLimite());
        Instant dataLimiteInstant = dataLimite.atStartOfDay(zoneRecife).toInstant();

        Acesso acesso = new Acesso();
        acesso.setAmbientes(sala);
        acesso.setUsuarios(usuario);
        acesso.setDataLimite(dataLimiteInstant);
        acesso.setHoraAcessoInicial(LocalTime.parse(request.horaAcessoInicial()));
        acesso.setHoraAcessoFinal(LocalTime.parse(request.horaAcessoFinal()));
        return acessoRepository.save(acesso);
    }

    public Acesso revogar(Long id) {
        Acesso acesso = acessoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Acesso não encontrado: " + id));
        ZoneId zoneRecife = ZoneId.of("America/Recife");
        Instant ontem = LocalDate.now(zoneRecife).minusDays(1).atStartOfDay(zoneRecife).toInstant();
        acesso.setDataLimite(ontem);
        return acessoRepository.save(acesso);
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
