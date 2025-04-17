package br.dee.trancasdee.respositories;

import br.dee.trancasdee.models.Ambientes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmbientesRepository extends JpaRepository<Ambientes, Long> {
    List<Ambientes> findAmbientesBySala(Ambientes ambientes);
}
